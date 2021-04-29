package com.mimacom.trainings.boot.kafkastreams.domain;

import lombok.Data;

/**
 * Very simple collision abstraction.
 * It represents result of situation where there was a combination
 * first and then decay later.
 * Two particles identified by idIn1 and idIn2 collide and 'produce'
 * intermediate state which then decays into two output constituents idOut1,idOut2.
 */
@Data
public class Collision {
    private String idIn1;
    private String idIn2;
    private String idIntermediate;
    private String idOut1;
    private String idOut2;
}
