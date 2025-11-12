package com.example.csm.controller;

import com.example.csm.api.CSMEventsApi;
import com.example.csm.model.CreateResponse;
import com.example.csm.model.FormCSMEventsRequest;
import com.example.csm.app.dto.MSCEmployeeEvent;
import com.example.csm.app.mapper.MSCEventMapper;
import com.example.csm.app.validation.FlattenResult;
import com.example.csm.app.validation.ValidationError;
import com.example.csm.app.service.MSCEventsService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class MSCEventsController implements CSMEventsApi {

    private final MSCEventsService service;

    public MSCEventsController(MSCEventsService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<CreateResponse> createCsmEvents(@Valid FormCSMEventsRequest body) {
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
