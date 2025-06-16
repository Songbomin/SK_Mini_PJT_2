package com.example.apigateway.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value(("${spring.data.redis.port}"))
    private int port;

    @Bean
    // 레디스 연결
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host); // Redis 서버 IP 주소
        config.setPort(port);              // Redis 서버 포트 번호
        return new LettuceConnectionFactory(config); // 연결 설정
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory()); // 연결 설정
        // 설정
        template.setKeySerializer(new StringRedisSerializer()); // 키를 저장할 때 문자열 객체 직렬화를 통해서 처리
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
