package com.example.msc.controller;

import com.example.msc.service.MscEventService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MscEventsController.class)
class MscEventsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MscEventService service;

    @Test
    void missingHeader_returns400() throws Exception {
        String json = "{ \"formMSCEvents\": [ { \"eventData\": {\"x\":1} } ] }";

        Mockito.doThrow(new IllegalArgumentException("header is mandatory"))
                .when(service).processEvents(Mockito.any());

        mockMvc.perform(post("/msc-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
}
