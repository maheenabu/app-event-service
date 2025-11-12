package com.example.csm.app.service;

import com.example.csm.app.dto.CsmEmployeeEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import jakarta.validation.ConstraintViolationException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig(classes = CsmEventsServiceImplTest.Config.class)
class CsmEventsServiceImplTest {

    @Configuration
    static class Config {
        @Bean CsmEventsService csmEventsService() { return new CsmEventsServiceImpl(); }
        @Bean MethodValidationPostProcessor methodValidationPostProcessor() { return new MethodValidationPostProcessor(); }
    }

    @Autowired
    private CsmEventsService service;

    @Test
    void processBatch_valid_noException() {
        CsmEmployeeEvent item = CsmEmployeeEvent.builder()
                .eventStatus("abc")
                .deliveryChannelId("1234")
                .svpSessionId("4545")
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
