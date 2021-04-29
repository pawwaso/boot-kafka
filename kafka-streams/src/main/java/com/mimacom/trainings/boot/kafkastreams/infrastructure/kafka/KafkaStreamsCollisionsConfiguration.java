package com.mimacom.trainings.boot.kafkastreams.infrastructure.kafka;

import com.mimacom.trainings.boot.kafkastreams.domain.Collision;
import com.mimacom.trainings.boot.kafkastreams.domain.Combination;
import com.mimacom.trainings.boot.kafkastreams.domain.Decay;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;
import java.util.function.BiFunction;

@Configuration
@Slf4j
@Profile("!test")
public class KafkaStreamsCollisionsConfiguration {

    /**
     * Consider this method's name change, what else should be changed.
     *
     * @return
     */
    @Bean
    public BiFunction<KStream<String, Combination>, KStream<String, Decay>, KStream<String, Collision>> processMimacom() {

        return (combinationStream, decayStream) ->

                combinationStream
                        //non-terminal logging
                        .peek((k, v) -> log.info("combination received k:{}, v:{}", k, v))
                        // we take only reasonable combinations
                        .filter((k, v) -> v != null && v.getIdOut() != null)
                        // rekey stream on the left by idOut
                        .map((k, v) -> KeyValue.pair(v.getIdOut(), v))
                        //now symmetric join
                        .join(
                                // with right rekeyed stream
                                decayStream
                                        //non-terminal logging
                                        .peek((k, v) -> log.info("decay received k:{}, v:{}", k, v))
                                        // we take only reasonably decays
                                        .filter((k, v) -> v != null && v.getIdIn() != null)
                                        //rekey by idIn
                                        .map((k, v) -> KeyValue.pair(v.getIdIn(), v)),
                                //join to produce collision
                                (com, dec) -> merge(com.getIdOut(), com, dec),
                                // within sliding window of given size
                                JoinWindows.of(Duration.ofSeconds(30)),
                                // define serdes for key and in-values
                                StreamJoined.with(
                                        Serdes.String(),
                                        new JsonSerde<>(Combination.class),
                                        new JsonSerde<>(Decay.class)

                                ))
                        .peek((k, v) -> log.info("output k:{}, v:{}", k, v));
    }

    private Collision merge(String key, Combination combination, Decay decay) {
        final Collision collision = new Collision();
        collision.setIdIn1(combination.getIdIn1());
        collision.setIdIn2(combination.getIdIn2());
        collision.setIdIntermediate(key);
        collision.setIdOut1(decay.getIdOut1());
        collision.setIdOut2(decay.getIdOut2());
        return collision;
    }
}
