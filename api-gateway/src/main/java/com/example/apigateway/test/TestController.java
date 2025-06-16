package com.example.apigateway.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisTestService redisTestService;

    @GetMapping("/test/redis")
    public String testRedis(@RequestParam String email) {
        System.out.println(email);
        //redisTestService.checkConnection();
        return (String) redisTemplate.opsForValue().get(email);
    }
}
