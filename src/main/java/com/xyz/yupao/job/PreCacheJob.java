package com.xyz.yupao.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyz.yupao.model.domain.User;
import com.xyz.yupao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class PreCacheJob {
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    //重点用户
    private List<Long> mainUserList= Arrays.asList(0L,1L,2L,3L);

    //每天执行，预热推荐用户 秒-分-时-日-月-年
    @Scheduled(cron = "0 23 2 * * *")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0,-1, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock:"+Thread.currentThread().getId());
                for (Long userId : mainUserList) {
                    // 默认查询前20个用户
                    Page<User> userPage = userService.page(new Page<>(1, 20), null);
                    // 写入缓存并设置12小时的缓存过期时间
                    String redisKey = String.format("yupao:user:recommend:%s", userId);
                    try {
                        redisTemplate.opsForValue().set(redisKey, userPage, 12, TimeUnit.HOURS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }

                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error ",e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()){
                System.out.println("unLock:"+Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
