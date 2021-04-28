package com.mimacom.trainings.boot.kafkaconsumer;

import com.mimacom.trainings.boot.kafkaconsumer.domain.SampleMessage;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

public class TestUtils {
    public static SampleMessage random(){
        final SampleMessage sampleMessage = new SampleMessage();
        sampleMessage.setKey(UUID.randomUUID().toString());
        sampleMessage.setValue(RandomStringUtils.randomAlphabetic(100));
        return
                sampleMessage;
    }
}
