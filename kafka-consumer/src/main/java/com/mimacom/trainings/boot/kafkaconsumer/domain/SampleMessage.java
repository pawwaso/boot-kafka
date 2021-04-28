package com.mimacom.trainings.boot.kafkaconsumer.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SampleMessage {

    private String key;
    private String value;
}
