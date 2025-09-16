package com.example.ai.flowise.memory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.beans.factory.annotation.Value;

/**
 * Redis配置类
 */
@Configuration
public class RedisConfig {

    @Value("${memory.max.chat.history:10}")
    private int maxChatHistory;

    @Value("${memory.ttl.minutes:10}")
    private int ttlMinutes;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // 使用StringRedisSerializer来序列化和反序列化redis的value值
        // 这样我们可以直接存储JSON字符串
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }

    public int getMaxChatHistory() {
        return maxChatHistory;
    }

    public int getTtlMinutes() {
        return ttlMinutes;
    }
}