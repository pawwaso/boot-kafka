package com.mimacom.trainings.boot.kafkaproducer.service;

import com.mimacom.trainings.boot.kafkaproducer.domain.SampleMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@RequiredArgsConstructor
@Slf4j
public class Publisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${app.kafka.topic.name}")
    private String topicName;

    public SampleMessage publish(SampleMessage message) {
        final ProducerRecord<String, String> record = new ProducerRecord<>(topicName, message.getKey(), message.getValue());
        kafkaTemplate.send(record)
                .addCallback(new ListenableFutureCallback<>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        log.error(" Exception while sending ", throwable);
                    }

                    @Override
                    public void onSuccess(SendResult<String, String> result) {
                        log.info("sent successfully: {}; result {}", message, result.getRecordMetadata());
                    }
                });
        return message;
    }

}
