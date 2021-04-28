package com.mimacom.trainings.boot.kafkaproducer.service;

import com.mimacom.trainings.boot.kafkaproducer.TestUtils;
import com.mimacom.trainings.boot.kafkaproducer.domain.SampleMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * In this test we use EmbeddedKafka to test kafka publisher. We prepare publisher and create test consumer.
 */
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(topics = {"test_topic_to_produce_to"})
@ActiveProfiles("test")
@Slf4j
class PublisherITest {
    //kafka test env
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    // we test this publisher
    private Publisher publisher;

    private Consumer<String, String> consumer;

    @Value("${app.kafka.topic.name}")
    private String topicName;

    @BeforeEach
    void setUp() {
        // test kafka producer factory
        ProducerFactory<String, String> producerFactory =
                new DefaultKafkaProducerFactory<>(
                        KafkaTestUtils.producerProps(this.embeddedKafkaBroker),
                        new StringSerializer(),
                        new StringSerializer());
        // directly inject into publisher
        publisher = new Publisher(new KafkaTemplate<>(producerFactory));
        // as we used 'new' we need to provide @value (topic name)
        ReflectionTestUtils.setField(publisher, "topicName", topicName);

        // create test kafka consumer
        final Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(UUID.randomUUID().toString(), "false", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // re-instantiate consumer to be used later in test
        consumer = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new StringDeserializer()).createConsumer();
    }

    @Test
    public void publish_oneMessage_successful() throws Exception {
        //arrange
        final SampleMessage sm = TestUtils.random();
        //act
      //  publisher.  // TODO invoke actual test method

        //assert
        final List<ConsumerRecord<String, String>> records = consumerRecords();
        Assertions.assertEquals(1, records.size());
        Assertions.assertEquals(sm.getKey(), records.get(0).key());
        Assertions.assertEquals(sm.getValue(), records.get(0).value());
    }

    private List<ConsumerRecord<String, String>> consumerRecords() {
      //  embeddedKafkaBroker.consumeFromAnEmbeddedTopic( //TODO force test consumer to consume
        final Iterable<ConsumerRecord<String, String>> iterableRecords=null;
       // iterableRecords= KafkaTestUtils.getRecords( // TODO get actual records
        final List<ConsumerRecord<String, String>> records = Lists.newArrayList(iterableRecords);
        log.info("results {}", records);
        return records;

    }
}
