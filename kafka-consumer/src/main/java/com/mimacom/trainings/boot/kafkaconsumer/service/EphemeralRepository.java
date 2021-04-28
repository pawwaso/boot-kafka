package com.mimacom.trainings.boot.kafkaconsumer.service;

import com.mimacom.trainings.boot.kafkaconsumer.domain.SampleMessage;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Storage simulator based on private map.
 */
@Component
public class EphemeralRepository {
    private Map<String, SampleMessage> storage = new HashMap<>();

    public SampleMessage save(SampleMessage sm) {
        this.storage.put(sm.getKey(), sm);
        return sm;
    }

    public List<SampleMessage> findAll() {
        return new ArrayList<>(this.storage.values());
    }

    public Optional<SampleMessage> findByKey(String key) {
        return Optional.ofNullable(this.storage.get(key));
    }
}
