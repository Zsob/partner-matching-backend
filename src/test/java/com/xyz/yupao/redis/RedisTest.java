package com.xyz.yupao.redis;

import com.xyz.yupao.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //插入
        valueOperations.set("TestString","dog");
        valueOperations.set("TestInt",1);
        valueOperations.set("TestDouble",2.1);
        User user = new User();
        user.setId(1);
        user.setUsername("zhangsan");
        valueOperations.set("TestUser",user);
        //查询
        Object test = valueOperations.get("TestString");
        Assertions.assertTrue("dog".equals((String) test));
        test=valueOperations.get("TestInt");
        Assertions.assertTrue(1==(Integer)test);
        test=valueOperations.get("TestDouble");
        Assertions.assertTrue(2.1==(Double)test);
        System.out.println(valueOperations.get("TestUser"));

    }
}
