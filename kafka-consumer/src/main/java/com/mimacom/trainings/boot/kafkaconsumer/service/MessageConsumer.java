package com.mimacom.trainings.boot.kafkaconsumer.service;

import com.mimacom.trainings.boot.kafkaconsumer.domain.SampleMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

    private final EphemeralRepository repository;

    @KafkaListener(topics = "")// TODO use placeholders to set up topic's name
    public void receive(@Payload SampleMessage sm,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        /* TODO let timestamp be injected too */ long timestamp) {
        log.info("consuming {}; topic {}; timestamp {}", sm,topic,timestamp);
        repository.save(sm);
    }
}
