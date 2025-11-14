package com.example.msc.service.validation;

import com.example.msc.model.EventHeader;
import com.example.msc.model.FormMSCEvent;
import com.example.msc.model.FormMSCEventsRequest;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MscEventValidatorTest {

    private final MscEventValidator validator = new MscEventValidator();

    @Test
    void validateAndGetEvents_validRequest_passes() {
        FormMSCEventsRequest req = new FormMSCEventsRequest();
        FormMSCEvent event = new FormMSCEvent();
        EventHeader header = new EventHeader();
        header.setEventStatus("OK");
        header.setDelChanId("123");
        event.setHeader(header);
        req.setFormMSCEvents(Collections.singletonList(event));

        assertDoesNotThrow(() -> validator.validateAndGetEvents(req));
        assertEquals(1, validator.validateAndGetEvents(req).size());
    }

    @Test
    void validateAndGetEvents_missingHeader_throws() {
        FormMSCEventsRequest req = new FormMSCEventsRequest();
        FormMSCEvent event = new FormMSCEvent();
        req.setFormMSCEvents(Collections.singletonList(event));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validateAndGetEvents(req));
        assertTrue(ex.getMessage().toLowerCase().contains("header"));
    }

    @Test
    void validateAndGetEvents_blankHeaderFields_throws() {
        FormMSCEventsRequest req = new FormMSCEventsRequest();
        FormMSCEvent event = new FormMSCEvent();
        EventHeader header = new EventHeader();
        header.setEventStatus("   ");
        header.setDelChanId("");
        event.setHeader(header);
        req.setFormMSCEvents(Collections.singletonList(event));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validateAndGetEvents(req));
        assertTrue(ex.getMessage().toLowerCase().contains("eventstatus"));
    }
}


    @Test
    void validateAndGetEvents_nullRequest_throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validateAndGetEvents(null));
        assertTrue(ex.getMessage().toLowerCase().contains("formmsc"));
    }
}
