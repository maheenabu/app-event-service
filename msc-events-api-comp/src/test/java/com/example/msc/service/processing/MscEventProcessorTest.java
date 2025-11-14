package com.example.msc.service.processing;

import com.example.msc.service.KafkaEventProducer;
import com.example.msc.service.mapping.MscEventContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MscEventProcessorTest {

    @Test
    void process_withValidContext_doesNotThrow() {
        KafkaEventProducer producer = new KafkaEventProducer("test-topic");
        ObjectMapper mapper = new ObjectMapper();
        MscEventProcessor processor = new MscEventProcessor(producer, mapper);

        Map<String, Object> dynamicBody = new HashMap<>();
        Map<String, Object> eventData = new HashMap<>();
        Map<String, Object> details = new HashMap<>();
        details.put("svpSessionId", "abc");
        details.put("formId", "sxx234");
        eventData.put("eventDetailsData", details);
        dynamicBody.put("eventData", eventData);

        MscEventContext ctx = new MscEventContext("OK", "123", dynamicBody);

        assertDoesNotThrow(() -> processor.process(ctx));
    }
}
