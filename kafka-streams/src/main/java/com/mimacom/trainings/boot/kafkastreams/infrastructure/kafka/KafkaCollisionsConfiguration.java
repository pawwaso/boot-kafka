package com.mimacom.trainings.boot.kafkastreams.infrastructure.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

import java.time.Duration;

@Configuration
@Profile("!test")
public class KafkaCollisionsConfiguration {

    @Value("${app.kafka.topic.input.first.name}")
    private String combinationTopic;

    @Value("${app.kafka.topic.input.second.name}")
    private String decayTopic;

    @Value("${app.kafka.topic.output.name}")
    private String outputTopic;


    @Bean
    public NewTopic createOutputTopic() {
        return TopicBuilder.name(outputTopic)
                .partitions(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(Duration.ofHours(1).toMillis()))
                .build();
    }


    @Bean
    public NewTopic createCombinationTopic() {
        return TopicBuilder.name(combinationTopic)
                .partitions(1)
                .build();
    }


    @Bean
    public NewTopic createDecayTopic() {
        return TopicBuilder.name(decayTopic)
                .partitions(1)
                .build();
    }
}
