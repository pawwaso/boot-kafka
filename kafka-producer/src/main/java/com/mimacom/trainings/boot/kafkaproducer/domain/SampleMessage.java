package com.mimacom.trainings.boot.kafkaproducer.domain;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
public class SampleMessage {

    private String key;
    @NotBlank
    private String value;
}
