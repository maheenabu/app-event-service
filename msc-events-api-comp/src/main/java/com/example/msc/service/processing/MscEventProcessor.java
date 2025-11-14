package com.example.msc.service.processing;

import com.example.msc.service.KafkaEventProducer;
import com.example.msc.service.mapping.MscEventContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class MscEventProcessor {

    private static final Logger log = LoggerFactory.getLogger(MscEventProcessor.class);

    private final KafkaEventProducer kafkaEventProducer;
    private final ObjectMapper objectMapper;

    public MscEventProcessor(KafkaEventProducer kafkaEventProducer,
                             ObjectMapper objectMapper) {
        this.kafkaEventProducer = kafkaEventProducer;
        this.objectMapper = objectMapper;
    }

    public void process(MscEventContext ctx) {

        Map<String, Object> dynamicBody = ctx.getDynamicBody();

        List<Map<String, String>> employees =
                DynamicExtractor.extractEmployees(dynamicBody);

        List<Map<String, String>> customers =
                DynamicExtractor.extractCustomers(dynamicBody);

        Map<String, String> eventDetailsData =
                DynamicExtractor.extractEventDetailsData(dynamicBody);

        log.info("Processing event: status={}, delChanId={}",
                ctx.getEventStatus(), ctx.getDelChanId());
        log.info("Employees: {}", employees);
        log.info("Customers: {}", customers);
        log.info("eventDetailsData: {}", eventDetailsData);

        Map<String, Object> message = new LinkedHashMap<>();
        Map<String, Object> headerMap = new LinkedHashMap<>();
        headerMap.put("eventStatus", ctx.getEventStatus());
        headerMap.put("delChanId", ctx.getDelChanId());
        message.put("header", headerMap);
        message.put("body", dynamicBody);

        try {
            String json = objectMapper.writeValueAsString(message);
            kafkaEventProducer.sendEvent(ctx.getDelChanId(), json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event for Kafka for delChanId={}",
                    ctx.getDelChanId(), e);
        }
    }
}
