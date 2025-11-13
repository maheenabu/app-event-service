package com.example.msc.service;

import com.example.msc.model.EventHeader;
import com.example.msc.model.FormMSCEvent;
import com.example.msc.model.FormMSCEventsRequest;
import com.example.msc.util.JsonTraversalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MscEventService {

    private static final Logger log = LoggerFactory.getLogger(MscEventService.class);

    public void processEvents(FormMSCEventsRequest request) {
        List<FormMSCEvent> events = Optional.ofNullable(request)
                .map(FormMSCEventsRequest::getFormMSCEvents)
                .orElseGet(Collections::emptyList);

        if (events.isEmpty()) {
            log.warn("No MSC events to process");
            return;
        }

        Map<Boolean, List<FormMSCEvent>> partitioned =
                events.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.partitioningBy(this::hasValidHeader));

        List<FormMSCEvent> validEvents = partitioned.getOrDefault(true, Collections.emptyList());
        List<FormMSCEvent> invalidEvents = partitioned.getOrDefault(false, Collections.emptyList());

        invalidEvents.forEach(e ->
                log.warn("Skipping event with invalid header: {}", e.getHeader())
        );

        validEvents.stream()
                .map(this::toEventContext)
                .forEach(this::dispatchEvent);
    }

    private boolean hasValidHeader(FormMSCEvent event) {
        return Optional.ofNullable(event.getHeader())
                .filter(h -> isNotBlank(h.getEventStatus()))
                .filter(h -> isNotBlank(h.getDelChanId()))
                .isPresent();
    }

    private boolean isNotBlank(String v) {
        return v != null && !v.trim().isEmpty();
    }

    private EventContext toEventContext(FormMSCEvent event) {
        EventHeader header = event.getHeader();
        Map<String, Object> dynamicBody =
                Optional.ofNullable(event.getAdditionalProperties()).orElseGet(Collections::emptyMap);

        return new EventContext(
                header.getEventStatus(),
                header.getDelChanId(),
                dynamicBody
        );
    }

    private void dispatchEvent(EventContext ctx) {
        Map<String, Object> dynamicBody = ctx.getDynamicBody();

        if (dynamicBody.isEmpty()) {
            log.info("No dynamic payload to process for delChanId={}", ctx.getDelChanId());
            return;
        }

        JsonTraversalUtil.traverse(dynamicBody, (path, value) ->
                log.info("Leaf at '{}' : {}", path, value)
        );
    }

    public static class EventContext {
        private final String eventStatus;
        private final String delChanId;
        private final Map<String, Object> dynamicBody;

        public EventContext(String eventStatus, String delChanId, Map<String, Object> dynamicBody) {
            this.eventStatus = eventStatus;
            this.delChanId = delChanId;
            this.dynamicBody = dynamicBody;
        }

        public String getEventStatus() {
            return eventStatus;
        }

        public String getDelChanId() {
            return delChanId;
        }

        public Map<String, Object> getDynamicBody() {
            return dynamicBody;
        }
    }
}
