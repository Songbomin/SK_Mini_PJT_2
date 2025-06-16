package com.example.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class ApiGatewayApplication {
    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayApplication.class);
    @Value("${gateway.km}")
    private String gatewayKm;

    @Value("${gateway.ps}")
    private String gatewayPs;

    @Value("${gateway.hUser}")
    private String gatewayHUser;

    @Value("${gateway.hMyPage}")
    private String gatewayHMyPage;

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator reUseRouteLocator(RouteLocatorBuilder builder) {
        logger.info("게이트웨이에서 개별 서비스 URL 등록");
        return builder.routes()
                .route("userService",
                        r -> r.path("/auth/**").uri(gatewayHUser)  )
                .route("userService",
                        r -> r.path("/user/**").uri(gatewayHUser)  )
                .route("myPageService",
                        r -> r.path("/myPage/**").uri(gatewayHMyPage)  )
                .route("myPageService",
                        r -> r.path("/home/**").uri(gatewayHMyPage)  )

                .route("sk_pjt2_back_chat",
                        r -> r.path("/test/**").uri(gatewayKm)  )

                // ms
                .route("sk_pjt2_back_chat",
                        r -> r.path("/chat/**").uri(gatewayKm)  )
                .route("sk_pjt2_back_chat",
                        r -> r.path("/room/**").uri(gatewayKm)  )
                .route("sk_pjt2_back_chat",
                        r -> r.path("/pub/**").uri(gatewayKm)  )
                .route("sk_pjt2_back_chat",
                        r -> r.path("/sub/**").uri(gatewayKm)  )
                .route("sk_pjt2_back_chat",
                        r -> r.path("/inquiry/**").uri(gatewayKm)  )
                // sj
                .route("sk_ptj2_products",
                        r -> r.path("/pdts/**").uri(gatewayPs)  )
                .route("sk_ptj2_products",
                        r -> r.path("/wishlist/**").uri(gatewayPs)  )
                .build();


    }
}
