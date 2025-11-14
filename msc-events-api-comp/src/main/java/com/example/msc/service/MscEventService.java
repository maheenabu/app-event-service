package com.example.msc.service;

import com.example.msc.model.FormMSCEvent;
import com.example.msc.model.FormMSCEventsRequest;
import com.example.msc.service.mapping.MscEventContext;
import com.example.msc.service.mapping.MscEventMapper;
import com.example.msc.service.processing.MscEventProcessor;
import com.example.msc.service.validation.MscEventValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MscEventService {

    private final MscEventValidator validator;
    private final MscEventMapper mapper;
    private final MscEventProcessor processor;

    public MscEventService(MscEventValidator validator,
                           MscEventMapper mapper,
                           MscEventProcessor processor) {
        this.validator = validator;
        this.mapper = mapper;
        this.processor = processor;
    }

    public void processEvents(FormMSCEventsRequest request) {
        List<FormMSCEvent> events = validator.validateAndGetEvents(request);

        events.stream()
                .map(mapper::toContext)
                .forEach(processor::process);
    }
}
