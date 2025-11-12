package com.example.msc.app.service;

import com.example.msc.app.dto.MSCEmployeeEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import jakarta.validation.ConstraintViolationException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig(classes = MSCEventsServiceImplTest.Config.class)
class MSCEventsServiceImplTest {

    @Configuration
    static class Config {
        @Bean
        MSCEventsService mscEventsService() { return new MSCEventsServiceImpl(); }
        @Bean MethodValidationPostProcessor methodValidationPostProcessor() { return new MethodValidationPostProcessor(); }
    }

    @Autowired
    private MSCEventsService service;

    @Test
    void processBatch_valid_noException() {
        MSCEmployeeEvent item = MSCEmployeeEvent.builder()
                .eventStatus("abc")
                .deliveryChannelId("1234")
                .sessionId("4545")
                .formId("7878")
                .empId("1566")
                .extras(Collections.singletonMap("role", "Engineer"))
                .build();
        service.processBatch(Collections.singletonList(item));
    }

    @Test
    void processBatch_empty_violates_NotEmpty() {
        assertThatThrownBy(() -> service.processBatch(Collections.emptyList()))
                .isInstanceOf(ConstraintViolationException.class);
    }
}
