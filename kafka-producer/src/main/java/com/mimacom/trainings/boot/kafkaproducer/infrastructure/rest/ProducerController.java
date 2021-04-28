package com.mimacom.trainings.boot.kafkaproducer.infrastructure.rest;

import com.mimacom.trainings.boot.kafkaproducer.domain.SampleMessage;
import com.mimacom.trainings.boot.kafkaproducer.service.Publisher;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Slf4j
public class ProducerController {
    private final Publisher publisher;

    @Operation(description = "produce messages to topic.")
    @PostMapping(value = "/messages",consumes = "application/json",produces = "application/json")
    public SampleMessage publishMessage(
            @NotNull @Valid @RequestBody SampleMessage message) {
        log.info("message {}", message);
        return publisher.publish(message);
    }
}
