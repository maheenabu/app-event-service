package com.example.msc.controller;

import com.example.msc.api.MscEventsApi;
import com.example.msc.model.FormMSCEventsRequest;
import com.example.msc.service.MscEventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MscEventsController implements MscEventsApi {

    private static final Logger log = LoggerFactory.getLogger(MscEventsController.class);

    private final MscEventService mscEventService;

    public MscEventsController(MscEventService mscEventService) {
        this.mscEventService = mscEventService;
    }

    @Override
    public ResponseEntity<Void> createMscEvents(@Valid FormMSCEventsRequest formMSCEventsRequest) {
        log.info("Received MSC events payload");
        mscEventService.processEvents(formMSCEventsRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
