package com.example.apigateway.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.stereotype.Service;

@Service
public class RedisTestService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void checkConnection() {
        try {
            RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
            String response = connection.ping();
            System.out.println("Redis connection successful: " + response);
        } catch (Exception e) {
            System.out.println("Failed to connect to Redis: " + e.getMessage());
        }
    }
}
