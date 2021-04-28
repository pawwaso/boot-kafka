package com.mimacom.trainings.boot.kafkaconsumer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * In this test we use EmbeddedKafka to test kafka consumer. We prepare consumer and create test producer.
 */
@SpringBootTest
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
@EmbeddedKafka(topics = {"test_topic_to_consume_from"})
@ActiveProfiles("test")
@Slf4j
class MessageConsumerITest {
    //kafka test env
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Value("${app.kafka.topic.name}")
    private String topicName;
    @MockBean
    EphemeralRepository repository;

    @Autowired
    MessageConsumer messageConsumer;
    Producer<String, String> producer;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        producer = new DefaultKafkaProducerFactory<>(configs,
                new StringSerializer(), new StringSerializer()).createProducer();
    }

    @Test
    void consumed_withKey_withPayload_ok() throws InterruptedException {
        //arrange act
        produceRecord("validKey", "{}");
        // assert
        assertConsumptionAtMost(
                () -> {
                    try {
                        Mockito.verify(repository).save(Mockito.any());
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
        );

    }

    @Test
    void consumed_withoutKey_withPayload_ok() throws InterruptedException {
        //arrange act
        produceRecord(null, "{}");
        // assert
        assertConsumptionAtMost(
                () -> {
                    try {
                        Mockito.verify(repository).save(Mockito.any());
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
        );
    }

    @Test
    void consumed_withKey_invalidPayload_errorHandled() throws InterruptedException {
        //arrange act
        produceRecord("key", "invalid");
        // assert
        assertConsumptionDelayed(
                () -> {
                    try {
                        Mockito.verifyNoInteractions(repository);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
        );

    }


    @Test
    void consumed_withKey_firstPayloadInvalid_secondOk() throws InterruptedException {
        //arrange act
        produceRecord("key", "invalid");
        produceRecord("key", "{}}");
        // assert
        assertConsumptionAtMost(
                () -> {
                    try {
                        Mockito.verify(repository).save(Mockito.any());
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
        );

    }

    private void produceRecord(String key, String payload) {
        producer.send(new ProducerRecord<>(topicName, key, payload));
        producer.flush();
    }

    private void assertConsumptionAtMost(Callable<Boolean> conditionEvaluator) {
        Awaitility.await()
                .atMost(Duration.FIVE_SECONDS)
                .with()
                .pollInterval(Duration.ONE_HUNDRED_MILLISECONDS)
                .until(conditionEvaluator);
    }

    private void assertConsumptionDelayed(Callable<Boolean> conditionEvaluator) {
        Awaitility.await()
                .pollDelay(Duration.TWO_SECONDS)
                .atMost(Duration.FIVE_SECONDS)
                .with()
                .until(conditionEvaluator);
    }
}
