package com.example.csm.controller;

import com.example.csm.app.service.CsmEventsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CsmEventsController.class)
class CsmEventsControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private CsmEventsService service;

    private String validJson() {
        return "{\n" +
                "  \"formCSMEvents\": [\n" +
                "    {\n" +
                "      \"header\": {\"eventStatus\": \"abc\", \"deliveryChannelId\": \"1234\"},\n" +
                "      \"eventData\": {\n" +
                "        \"eventDetailData\": {\"svpSessionId\": \"4545\", \"formId\": \"7878\"},\n" +
                "        \"employees\": [\n" +
                "          {\"empId\": \"1566\", \"role\": \"Engineer\"}\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    @Test
    void createCsmEvents_valid_201_and_calls_service() throws Exception {
        Mockito.doNothing().when(service).processBatch(anyList());

        mockMvc.perform(post("/api/v1/csm-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("accepted"))
                .andExpect(jsonPath("$.correlationId").exists());
    }

    @Test
    void createCsmEvents_missing_employees_400() throws Exception {
        String invalid = "{\n" +
                "  \"formCSMEvents\": [\n" +
                "    {\n" +
                "      \"header\": {\"eventStatus\": \"abc\", \"deliveryChannelId\": \"1234\"},\n" +
                "      \"eventData\": {\"eventDetailData\": {\"svpSessionId\": \"4545\", \"formId\": \"7878\"}}\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        mockMvc.perform(post("/api/v1/csm-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.correlationId").exists());
    }
}
