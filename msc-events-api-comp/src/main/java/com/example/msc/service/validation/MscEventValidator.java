package com.example.msc.service.validation;

import com.example.msc.model.EventHeader;
import com.example.msc.model.FormMSCEvent;
import com.example.msc.model.FormMSCEventsRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class MscEventValidator {

    public List<FormMSCEvent> validateAndGetEvents(FormMSCEventsRequest request) {

        List<FormMSCEvent> events = Optional.ofNullable(request)
                .map(FormMSCEventsRequest::getFormMSCEvents)
                .orElse(Collections.emptyList());

        if (events.isEmpty()) {
            throw new IllegalArgumentException("formMSCEvents must not be empty");
        }

        boolean missingHeader = events.stream()
                .anyMatch(e -> e == null || e.getHeader() == null);

        if (missingHeader) {
            throw new IllegalArgumentException("header is mandatory for each MSC event");
        }

        boolean invalidHeader = events.stream()
                .map(FormMSCEvent::getHeader)
                .anyMatch(this::isInvalidHeader);

        if (invalidHeader) {
            throw new IllegalArgumentException("eventStatus and delChanId must be non-blank in header");
        }

        return events;
    }

    private boolean isInvalidHeader(EventHeader header) {
        return isBlank(header.getEventStatus()) || isBlank(header.getDelChanId());
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
