package com.mimacom.trainings.boot.kafkastreams.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimacom.trainings.boot.kafkastreams.domain.Combination;
import com.mimacom.trainings.boot.kafkastreams.domain.Decay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Simulator.
 * Decay and combination topics producer.
 * Produces random entries to both topics. MEdssages are produces with fixed delay.
 */
// TODO enable this publisher
@Slf4j
@Profile("!test")
@RequiredArgsConstructor
public class Publisher {
    private static final List<String> PARTICLES = List.of(
            "electron", "neutrino", "muon", "u", "d", "s"
    );

    @Value("${app.kafka.topic.input.first.name}")
    private String combinations;

    @Value("${app.kafka.topic.input.second.name}")
    private String decays;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    @Scheduled(fixedDelay = 11000)
    public void publishCombination() throws JsonProcessingException {
        final ProducerRecord<String, String> record = new ProducerRecord<>(
                combinations,
                UUID.randomUUID().toString(),
                mapper.writeValueAsString(newCombination()));
        kafkaTemplate.send(record);
        log.info("combination sent");
    }

    @Scheduled(fixedDelay = 10010)
    public void publishDecay() throws JsonProcessingException {
        final ProducerRecord<String, String> record = new ProducerRecord<>(
                decays,
                UUID.randomUUID().toString(),
                mapper.writeValueAsString(newDecay()));
        kafkaTemplate.send(record);
        log.info("decay sent");
    }


    private Combination newCombination() {
        final Combination combination = new Combination();
        combination.setIdIn1(randomParticle());
        combination.setIdIn2(randomParticle());
        combination.setIdOut(randomParticle());
        return combination;
    }

    private Decay newDecay() {
        final Decay decay = new Decay();
        decay.setIdOut2(randomParticle());
        decay.setIdOut1(randomParticle());
        decay.setIdIn(randomParticle());
        return decay;
    }

    private String randomParticle() {
        return PARTICLES.get(RandomUtils.nextInt(0, PARTICLES.size()));
    }
}
