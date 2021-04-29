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
     * TODO Consider renaming this method, what else should be changed.
     *
     */
    @Bean
    public BiFunction<KStream<String, Combination>, KStream<String, Decay>, KStream<String, Collision>> processMimacom() {

        return (combinationStream, decayStream) ->

                combinationStream
                        //non-terminal logging
                     //TODO  add   peek(.. to log what's been received
                        // we take only reasonable combinations
                       // TODO   implement .filter(... to filter out null values and null 'idOut'
                        // rekey stream on the left by idOut
                        .map((k, v) -> KeyValue.pair(v.getIdOut(), v))
                        //now symmetric join
                        .join(
                                // with right rekeyed stream
                                decayStream
                                        //non-terminal logging
                                        //TODO  add   peek(.. to log what's been received in decays topic
                                        // we take only reasonably decays
                                        // TODO   implement .filter(... to filter out null values and null 'idIn'
                                        //rekey by idIn
                                        .map((k, v) -> KeyValue.pair(v.getIdIn(), v)),
                                //join to produce collision
                                (com, dec) -> /* TODO call  actual join use 'this.merge()' method */ null,
                                // within sliding window of given size
                                /* TODO define window JoinWindows.... */ null,
                                // define serdes for key and in-values
                                StreamJoined.with(
                                        Serdes.String(),
                                        new JsonSerde<>(Combination.class),
                                        new JsonSerde<>(Decay.class)

                                ))
                //TODO  add   peek(.. to log what's about to be published
                ;
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
