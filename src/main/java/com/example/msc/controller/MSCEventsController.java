package com.example.msc.controller;

import com.example.msc.api.MSCEventsApi;
import com.example.msc.model.CreateResponse;
import com.example.msc.model.FormMSCEventsRequest;
import com.example.msc.app.dto.MSCEmployeeEvent;
import com.example.msc.app.mapper.MSCEventMapper;
import com.example.msc.app.validation.FlattenResult;
import com.example.msc.app.validation.ValidationError;
import com.example.msc.app.service.MSCEventsService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class MSCEventsController implements MSCEventsApi {

    private final MSCEventsService service;

    public MSCEventsController(MSCEventsService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<CreateResponse> createMSCEvents(@Valid FormMSCEventsRequest body) {
        final String corrId = UUID.randomUUID().toString();

        FlattenResult<MSCEmployeeEvent> res = MSCEventMapper.validateAndFlatten(body);

        if (!res.isOk()) {
            String message = res.getErrors().stream()
                    .map(ValidationError::toString)
                    .collect(Collectors.joining("; "));

            return ResponseEntity.badRequest().body(
                new CreateResponse().status("validation_failed: " + message)
                                    .correlationId(corrId)
            );
        }

        service.processBatch(res.getItems());

        return ResponseEntity.status(201).body(
            new CreateResponse().status("accepted").correlationId(corrId)
        );
    }
}
