package com.mimacom.trainings.boot.kafkastreams;

import com.mimacom.trainings.boot.kafkastreams.domain.Combination;
import com.mimacom.trainings.boot.kafkastreams.domain.Decay;

import java.util.UUID;

public class TestUtils {
    public static Decay randomDecay() {
        return randomDecay(UUID.randomUUID().toString());
    }

    public static Decay randomDecay(String idIn) {
        final Decay decay = new Decay();
        decay.setIdIn(idIn);
        decay.setIdOut1(UUID.randomUUID().toString());
        decay.setIdOut2(UUID.randomUUID().toString());
        return decay;
    }

    public static Combination randomCombination() {
        return randomCombination(UUID.randomUUID().toString());
    }

    public static Combination randomCombination(String idOut) {
        final Combination combination = new Combination();
        combination.setIdIn1(UUID.randomUUID().toString());
        combination.setIdIn2(UUID.randomUUID().toString());
        combination.setIdOut(idOut);
        return combination;
    }
}
