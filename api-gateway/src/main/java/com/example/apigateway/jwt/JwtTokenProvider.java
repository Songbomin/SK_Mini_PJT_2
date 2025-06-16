package com.example.apigateway.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.token.raw_secret_key}")
    private String rawSecretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey secretKey;

    // 처음 실행 될 때 1번만 자동 호출되는 함수
    @PostConstruct
    public void init() {
        logger.info("게이트웨이에서 시크릿 초기화 처리");
        // 시크릿 키 바이트로 변환
        this.secretKey = Keys.hmacShaKeyFor(rawSecretKey.getBytes());
    }

    public String createToken(String email) {
        // 이메일을 통해 사용자를 식별함 -> 이베일을 기준으로 다른 토큰이 만들어짐
        Claims claims = Jwts.claims().setSubject(email);
        // 맵에 이메일 넣어주기
        claims.put("email", email);

        Date now = new Date();
        // 만료기간 설정
        Date expirationDate = new Date(now.getTime() + expiration);
        logger.info("새로운 토큰 생성 중");

        return Jwts.builder()
                // 추가 정보 기입
                .setClaims(claims)
                // 등록 일자
                .setIssuedAt(now)
                // 만료 일자
                .setExpiration(expirationDate)
                // 아래의 비밀키와 hs256 알고리즘을 통해서 서명함
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

//    public boolean validateToken(String token) {
//        try{
//            // 서명에서 사용한 비밀키로 값을 얻을 수 있으면 true
//            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }

    public String getEmailFromToken(String token) {
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey).build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("email", String.class);
        } catch (ExpiredJwtException e) {
            logger.error("토큰 기간 만료");
            throw e;
        } catch (Exception e) {
            logger.error("토큰 디코딩 불가");
            throw e;
        }
    }
}
