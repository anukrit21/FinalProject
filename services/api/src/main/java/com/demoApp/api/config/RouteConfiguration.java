package com.demoApp.apigateway.config;

import com.demoApp.apigateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfiguration {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // User service routes
                .route("user-service", r -> r.path("/api/users/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .rewritePath("/api/users/(?<segment>.*)", "/${segment}"))
                        .uri("lb://user-service"))
                
                // Auth service routes - No auth filter for login/register
                .route("auth-service", r -> r.path("/api/auth/**")
                        .filters(f -> f.rewritePath("/api/auth/(?<segment>.*)", "/${segment}"))
                        .uri("lb://auth-service"))

                // Owner service routes
                .route("owner-service", r -> r.path("/api/owners/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .rewritePath("/api/owners/(?<segment>.*)", "/${segment}"))
                        .uri("lb://owner-service"))

                // Mess service routes
                .route("mess-service", r -> r.path("/api/mess/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .rewritePath("/api/mess/(?<segment>.*)", "/${segment}"))
                        .uri("lb://mess-service"))

                // OTP service routes - Some endpoints don't need auth
                .route("otp-service", r -> r.path("/api/otp/**")
                        .filters(f -> f.rewritePath("/api/otp/(?<segment>.*)", "/${segment}"))
                        .uri("lb://otp-service"))

                // Subscription service routes
                .route("subscription-service", r -> r.path("/api/subscriptions/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .rewritePath("/api/subscriptions/(?<segment>.*)", "/${segment}"))
                        .uri("lb://subscription-service"))

                // Payment service routes
                .route("payment-service", r -> r.path("/api/payments/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .rewritePath("/api/payments/(?<segment>.*)", "/${segment}"))
                        .uri("lb://payment-service"))

                // Admin service routes
                .route("admin-service", r -> r.path("/api/admin/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .rewritePath("/api/admin/(?<segment>.*)", "/${segment}"))
                        .uri("lb://admin-service"))
                        
                // Menu service routes
                .route("menu-service", r -> r.path("/api/menu/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .rewritePath("/api/menu/(?<segment>.*)", "/${segment}"))
                        .uri("lb://menu-service"))
                        
                // Campus service routes
                .route("campus-service", r -> r.path("/api/campus/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .rewritePath("/api/campus/(?<segment>.*)", "/${segment}"))
                        .uri("lb://campus-service"))
                        
                // Delivery service routes
                .route("delivery-service", r -> r.path("/api/delivery/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .rewritePath("/api/delivery/(?<segment>.*)", "/${segment}"))
                        .uri("lb://delivery-service"))
                .build();
    }
} 