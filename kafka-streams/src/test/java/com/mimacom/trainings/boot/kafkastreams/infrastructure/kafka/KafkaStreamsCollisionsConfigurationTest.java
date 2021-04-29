package com.mimacom.trainings.boot.kafkastreams.infrastructure.kafka;

import com.mimacom.trainings.boot.kafkastreams.TestUtils;
import com.mimacom.trainings.boot.kafkastreams.domain.Collision;
import com.mimacom.trainings.boot.kafkastreams.domain.Combination;
import com.mimacom.trainings.boot.kafkastreams.domain.Decay;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;

@Slf4j
class KafkaStreamsCollisionsConfigurationTest {
    static final String DECAYS = "decays";
    static final String COMBINATIONS = "combinations";
    static final String OUTPUT = "output";
    //test in- out-topics
    TestInputTopic<String, Combination> combinationTopic;
    TestInputTopic<String, Decay> decayTopic;
    TestOutputTopic<String, Collision> outputTopic;

    final Serde<String> stringSerde = Serdes.String();
    // kafka stream test runner
    private TopologyTestDriver testDriver;
    // actual stream provider under test
    private KafkaStreamsCollisionsConfiguration kafkaStreamsCollisionsConfiguration;


    @AfterEach
    public void tearDown() {
        try {
            testDriver.close();
        } catch (final RuntimeException e) {
            log.info("Ignoring exception", e);
        }
    }

    @BeforeEach
    public void setup() {

        kafkaStreamsCollisionsConfiguration = new KafkaStreamsCollisionsConfiguration();
        final StreamsBuilder builder = new StreamsBuilder();
        buildStreamProcessingPipeline(builder);

        testDriver = new TopologyTestDriver(builder.build(), getStreamsConfiguration());
        combinationTopic = testDriver.createInputTopic(COMBINATIONS, stringSerde.serializer(), KafkaStreamsCollisionsConfigurationTest.<Combination>jsonSerde().serializer());
        decayTopic = testDriver.createInputTopic(DECAYS, stringSerde.serializer(), KafkaStreamsCollisionsConfigurationTest.<Decay>jsonSerde().serializer());
        outputTopic = testDriver.createOutputTopic(OUTPUT, stringSerde.deserializer(), KafkaStreamsCollisionsConfigurationTest.<Collision>jsonSerde().deserializer());
    }


    @Test
    void singleCombination_singleDecay_idsNotMatch_nothingReturned() {

        decayTopic.pipeInput("any", TestUtils.randomDecay());
        combinationTopic.pipeInput("other", TestUtils.randomCombination());

        //assert
        Assertions.assertTrue(outputTopic.isEmpty());
    }

    @Test
    void nullValues_filteredOut_empty() {

        decayTopic.pipeInput("any",null);
        combinationTopic.pipeInput("other", null);

        //assert
        Assertions.assertTrue(outputTopic.isEmpty());
    }

    @Test
    void singleCombination_singleDecay_idsMatch_oneReturned() {
        //arrange
        final String matchingId = "matchingId";
        final Decay decay = TestUtils.randomDecay(matchingId);
        final Combination combination = TestUtils.randomCombination(matchingId);
        //act
        decayTopic.pipeInput("any", decay);
        combinationTopic.pipeInput("other", combination);

        //assert
        Assertions.assertFalse(outputTopic.isEmpty());
        final Collision collision = outputTopic.readValue();
        Assertions.assertEquals(matchingId, collision.getIdIntermediate());
        Assertions.assertEquals(decay.getIdOut1(), collision.getIdOut1());
        Assertions.assertEquals(combination.getIdIn1(), collision.getIdIn1());
    }


    @Test
    void twoCombinations_singleDecay_idsMatch_twoReturned() {
        //arrange
       // TODO implement actual test matching assertions

        //assert
        Assertions.assertFalse(outputTopic.isEmpty());
        final List<Collision> collisions = outputTopic.readValuesToList();
        Assertions.assertEquals(2, collisions.size());
    }

    /**
     * stream test properties definition
     *
     * @return
     */
    private static Properties getStreamsConfiguration() {
        final Properties streamsConfiguration = new Properties();
        // Need to be set even these do not matter with TopologyTestDriver
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "TopologyTestDriverMimacomCollisionSnapshot");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "irrelevant:9092");
        return streamsConfiguration;
    }

    /**
     * actual binding to test topics
     *
     * @param builder
     */
    private void buildStreamProcessingPipeline(StreamsBuilder builder) {
        KStream<String, Combination> combinations = builder.stream(COMBINATIONS, Consumed.with(stringSerde, jsonSerde()));
        KStream<String, Decay> decays = builder.stream(DECAYS, Consumed.with(stringSerde, jsonSerde()));

        final BiFunction<KStream<String, Combination>, KStream<String, Decay>, KStream<String, Collision>>
                kStreamKStreamKStreamBiFunction = kafkaStreamsCollisionsConfiguration.processMimacom();
        KStream<String, Collision> output = kStreamKStreamKStreamBiFunction.apply(combinations, decays);
        output.to(OUTPUT, Produced.with(stringSerde, jsonSerde()));
    }

    private static <T> JsonSerde<T> jsonSerde() {
        JsonSerde<T> jsonSerde = new JsonSerde<>();
        ((JsonDeserializer) jsonSerde.deserializer()).addTrustedPackages("*");
        return jsonSerde;
    }
}
