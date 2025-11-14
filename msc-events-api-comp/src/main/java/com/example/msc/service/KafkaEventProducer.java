package com.example.msc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public KafkaEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                              @Value("${app.kafka.topic.msc-events:msc-events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    // Convenience constructor for tests
    public KafkaEventProducer(String topic) {
        this.kafkaTemplate = null;
        this.topic = topic;
    }

    public void sendEvent(String key, String payload) {
        log.debug("Sending event to Kafka topic={} key={}", topic, key);
        if (kafkaTemplate != null) {
            kafkaTemplate.send(topic, key, payload);
        } else {
            log.debug("KafkaTemplate is null (probably in tests), skipping send");
        }
    }
}
