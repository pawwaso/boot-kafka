package com.mimacom.trainings.boot.kafkastreams.domain;

import lombok.Data;

/**
 * Very simple decay abstraction.
 * Single particle identified by idIn decays into
 * two particles identified by idOut1, idOut2.
 */
@Data
public class Decay {
    private String idIn;
    private String idOut1;
    private String idOut2;
}
