package com.mimacom.trainings.boot.kafkaconsumer.infrastructure.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.LoggingErrorHandler;

@Configuration
public class KafkaConsumerConfig {
    /**
     * As we leverage ErrorHandlingDeserializer we define what to do in case of
     * deserialization problems
     */
    @Bean
    public LoggingErrorHandler errorHandler() {
        return new LoggingErrorHandler();
    }
}
