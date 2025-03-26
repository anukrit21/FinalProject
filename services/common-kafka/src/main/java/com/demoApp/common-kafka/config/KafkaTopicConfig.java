package com.demoApp.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    // User service related topics
    @Bean
    public NewTopic userCreatedTopic() {
        return TopicBuilder.name("user-created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userUpdatedTopic() {
        return TopicBuilder.name("user-updated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Authentication service related topics
    @Bean
    public NewTopic authSuccessTopic() {
        return TopicBuilder.name("auth-success")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic authFailureTopic() {
        return TopicBuilder.name("auth-failure")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // OTP service related topics
    @Bean
    public NewTopic otpGeneratedTopic() {
        return TopicBuilder.name("otp-generated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic otpVerifiedTopic() {
        return TopicBuilder.name("otp-verified")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Menu service related topics
    @Bean
    public NewTopic menuUpdatedTopic() {
        return TopicBuilder.name("menu-updated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Mess service related topics
    @Bean
    public NewTopic messCreatedTopic() {
        return TopicBuilder.name("mess-created")
                .partitions(3)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic messUpdatedTopic() {
        return TopicBuilder.name("mess-updated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Subscription service related topics
    @Bean
    public NewTopic subscriptionCreatedTopic() {
        return TopicBuilder.name("subscription-created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic subscriptionUpdatedTopic() {
        return TopicBuilder.name("subscription-updated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic subscriptionCanceledTopic() {
        return TopicBuilder.name("subscription-canceled")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Payment service related topics
    @Bean
    public NewTopic paymentProcessedTopic() {
        return TopicBuilder.name("payment-processed")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return TopicBuilder.name("payment-failed")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Delivery service related topics
    @Bean
    public NewTopic deliveryAssignedTopic() {
        return TopicBuilder.name("delivery-assigned")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deliveryCompletedTopic() {
        return TopicBuilder.name("delivery-completed")
                .partitions(3)
                .replicas(1)
                .build();
    }
} 