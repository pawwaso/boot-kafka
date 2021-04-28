package com.mimacom.trainings.boot.kafkaproducer.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimacom.trainings.boot.kafkaproducer.TestUtils;
import com.mimacom.trainings.boot.kafkaproducer.domain.SampleMessage;
import com.mimacom.trainings.boot.kafkaproducer.service.Publisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class ProducerControllerITest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    private Publisher publisher;
    private static final String API_MESSAGES = "/api/messages";

    @Test
    void postMessage_ok() throws Exception {
        //arrange
        //act assert
        final SampleMessage sm = TestUtils.random();
        this.mockMvc.
                perform(MockMvcRequestBuilders.post(API_MESSAGES)
                        .content(mapper.writeValueAsString(sm))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).
                andDo(MockMvcResultHandlers.print()).
                andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    void postMessage_valueMissing_nok() throws Exception {
        //arrange
        final SampleMessage sm = TestUtils.random();
        //act assert
        sm.setValue(null);
        this.mockMvc.
                perform(MockMvcRequestBuilders.post(API_MESSAGES)
                        .content(mapper.writeValueAsString(sm))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).
                andDo(MockMvcResultHandlers.print()).
                andExpect(MockMvcResultMatchers.status().isBadRequest());
        Assertions.assertNotNull(sm);
    }

}
