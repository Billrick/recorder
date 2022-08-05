package com.rick.utils.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory){

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        StringRedisSerializer serializer = new StringRedisSerializer();
        // 使用自定义序列化方式
        MyRedisSerializer myRedisSerializer = new MyRedisSerializer();

        template.setKeySerializer(serializer);
        template.setValueSerializer(myRedisSerializer);

        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(myRedisSerializer);

        return template;
    }

}