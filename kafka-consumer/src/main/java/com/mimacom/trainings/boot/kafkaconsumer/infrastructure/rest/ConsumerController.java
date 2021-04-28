package com.mimacom.trainings.boot.kafkaconsumer.infrastructure.rest;

import com.mimacom.trainings.boot.kafkaconsumer.domain.SampleMessage;
import com.mimacom.trainings.boot.kafkaconsumer.service.EphemeralRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Slf4j
public class ConsumerController {
    private final EphemeralRepository repository;

    @Operation(description = "get consumed messages.")
    @GetMapping(value = "/messages", produces = "application/json")
    public List<SampleMessage> getMessages() {
        final List<SampleMessage> all = repository.findAll();
        log.info("all messages {}", all.size());
        return all;
    }
}
