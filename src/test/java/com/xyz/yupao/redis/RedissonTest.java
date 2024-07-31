package com.xyz.yupao.redis;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;

    @Test
    void test(){
        // list 数据存放在本地 JVM 内存中
        List<String> list=new ArrayList<>();
        list.add("test");
        System.out.println("list:"+list.get(0));

        // 数据存在 redis 内存中
        RList<Object> rList = redissonClient.getList("test-list");
        rList.add("test");
        System.out.println("rList:"+rList.get(0));

        // map
        RMap<Object, Object> map = redissonClient.getMap("test-map");
        map.put("k1","v1");

    }

    @Test
    void testWatchDog(){
        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");
        try {
            if (lock.tryLock(0,-1, TimeUnit.MILLISECONDS)){
                // 休眠3000秒 -> 50分钟
                Thread.sleep(3000000);
                System.out.println("getLock:"+Thread.currentThread().getId());
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()){
                System.out.println("unLock:"+Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
