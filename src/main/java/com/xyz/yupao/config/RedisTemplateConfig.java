package com.xyz.yupao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisTemplateConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory connectionFactory){
        // 创建RedisTemplate对象，指定key类型为String，value类型为Object
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置Redis连接工厂，这是Redis操作的核心依赖
        redisTemplate.setConnectionFactory(connectionFactory);
        // 使用String序列化器对Key进行序列化，以保证key的存储格式为字符串
        redisTemplate.setKeySerializer(RedisSerializer.string());
        // 使用GenericJackson2JsonRedisSerializer序列化器对值进行JSON序列化
        // 保证Value以Json格式存储
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        return redisTemplate;
    }
}
