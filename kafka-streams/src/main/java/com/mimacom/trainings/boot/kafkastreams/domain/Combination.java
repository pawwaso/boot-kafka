package com.mimacom.trainings.boot.kafkastreams.domain;

import lombok.Data;

/**
 * Very simple collision (combination) abstraction.
 * Two particles identified by idIn1 and idIn2 collide and 'produce'
 * third particle identified by idOut
 */
@Data
public class Combination {
    private String idIn1;
    private String idIn2;
    private String idOut;
}
