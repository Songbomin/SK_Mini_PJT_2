package com.example.apigateway.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
public class JwtFilter implements WebFilter, ApplicationContextAware {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private JwtTokenProvider jwtTokenProvider;
    public JwtFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        logger.info("JWT Filter 정상 호출");

        String reqUrl = exchange.getRequest().getURI().getPath();
        logger.info("요청 URL: " + reqUrl);

        // 토큰 정보 획득
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        if(token != null) {
            try {
                String email = jwtTokenProvider.getEmailFromToken(token);
                logger.info("email 추출 : " + email);

                // 스프링 시큐리티에서 인증을 위한 기본 객체 생성
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        new User( email, "", new ArrayList<>()), null, null
                );
                return chain.filter(
                        exchange.mutate().request(
                                exchange.getRequest()
                                        .mutate().header("X-Auth-User", email)
                                        .build())
                                .build())
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
            } catch (ExpiredJwtException e) {
                // 엑세스 토큰이 만료가 됐으면
                // 레디스에서 리프레쉬 토큰이 있으면 바로 발급
                logger.info("엑세스 토큰 만료 -> 기존 토큰: " + token);
                String email = e.getClaims().get("email", String.class);
                String refreshToken = (String) redisTemplate.opsForValue().get(email);
                if(refreshToken != null) {
                    String newAccessToken = jwtTokenProvider.createToken(email);
                    logger.info("엑세스 토큰 만료 -> 신규 토큰: " + newAccessToken);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            new User( email, "", new ArrayList<>()), null, null
                    );
                    return chain.filter(
                                    exchange.mutate().request(
                                                    exchange.getRequest()
                                                            .mutate()
                                                                .header("X-Auth-User", email)
                                                                .header("Authorization", newAccessToken)
                                                            .build())
                                            .build())
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                }
                else{
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "리프레쉬 토큰이 만료되었습니다. 다시 로그인 해주세요");
                }


            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // 토큰이 없으면 시큐리티 콘피그 체인으로 줘버림
        return chain.filter(exchange);
    }
}
