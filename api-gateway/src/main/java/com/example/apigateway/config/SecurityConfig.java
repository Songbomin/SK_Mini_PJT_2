package com.example.apigateway.config;

import com.example.apigateway.handler.CustomAccessDeniedHandler;
import com.example.apigateway.handler.CustomAuthenticationEntryPoint;
import com.example.apigateway.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .cors(corsSpec -> corsSpec.configurationSource(request -> {
                    CorsConfiguration corsConfig = new CorsConfiguration();
                    corsConfig.addAllowedOrigin("http://localhost:3000"); // 리액트 클라이언트 도메인
                    corsConfig.addAllowedOrigin("http://183.111.123.137:3000");
                    corsConfig.addAllowedMethod("*");  // 모든 HTTP 메서드 허용 (GET, POST, PUT, DELETE 등)
                    corsConfig.addAllowedHeader("*");  // 모든 헤더 허용
                    corsConfig.setAllowCredentials(true);  // 자격 증명 포함 허용
                    corsConfig.addExposedHeader("AccessToken");
                    corsConfig.addExposedHeader("X-Auth-User");
                    return corsConfig;
                }))

                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // 헤더 설정 -> iframe 비활성화
                .headers(header -> header.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable))

                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("/user/**", "/auth/**","/pdts/**","/home/**"
                                ,"/room/**","/chat/**","/wishlist/**"
//                                "/pub/**","/sub/**",
//                                "/chat/**","/inquiry/**"
                        )
                        .permitAll()


                        .anyExchange().authenticated() )
                .exceptionHandling(exception -> {
                    exception
                            .accessDeniedHandler(new CustomAccessDeniedHandler()) //403
                            .authenticationEntryPoint(new CustomAuthenticationEntryPoint()); //401
                })

                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHORIZATION);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
