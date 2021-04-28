package com.mimacom.trainings.boot.kafkaconsumer.infrastructure.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Value("${app.kafka.topic.name}")
    private String topicName;
    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name(topicName).build();
    }

}
