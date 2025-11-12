package com.example.csm.app.mapper;

import com.example.csm.app.dto.MSCEmployeeEvent;
import com.example.csm.app.validation.FlattenResult;
import com.example.csm.app.validation.ValidationError;
import com.example.csm.model.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class MSCEventMapperTest {

    private FormCSMEventsRequest validRequest() {
        Header header = new Header();
        header.setEventStatus("abc");
        header.setDeliveryChannelId("1234");

        EventDetailData detail = new EventDetailData();
        detail.setSvpSessionId("4545");
        detail.setFormId("7878");

        Employee e1 = new Employee();
        e1.setEmpId("1566");
        Map<String,Object> extras = new HashMap<>();
        extras.put("role", "Engineer");
        e1.setAdditionalProperties(extras);

        EventData data = new EventData();
        data.setEventDetailData(detail);
        data.setEmployees(Collections.singletonList(e1));

        FormCSMEvent event = new FormCSMEvent();
        event.setHeader(header);
        event.setEventData(data);

        FormCSMEventsRequest req = new FormCSMEventsRequest();
        req.setFormCSMEvents(Collections.singletonList(event));
        return req;
    }

    @Test
    void validateAndFlatten_valid_ok() {
        FlattenResult<MSCEmployeeEvent> result = MSCEventMapper.validateAndFlatten(validRequest());
        assertThat(result.isOk()).isTrue();
        assertThat(result.getErrors()).isEmpty();
        assertThat(result.getItems()).hasSize(1);
        MSCEmployeeEvent row = result.getItems().get(0);
        assertThat(row.getEmpId()).isEqualTo("1566");
        assertThat(row.getEventStatus()).isEqualTo("abc");
        assertThat(row.getDeliveryChannelId()).isEqualTo("1234");
        assertThat(row.getSvpSessionId()).isEqualTo("4545");
        assertThat(row.getFormId()).isEqualTo("7878");
        assertThat(row.getExtras()).containsEntry("role", "Engineer");
    }

    @Test
    void validateAndFlatten_missing_formCSMEvents_errors() {
        FlattenResult<MSCEmployeeEvent> result = MSCEventMapper.validateAndFlatten(new FormCSMEventsRequest());
        assertThat(result.isOk()).isFalse();
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getErrors())
                .extracting(ValidationError::getPath)
                .containsExactly("formCSMEvents");
    }

    @Test
    void validateAndFlatten_missing_header_errors() {
        FormCSMEventsRequest req = validRequest();
        req.getFormCSMEvents().get(0).setHeader(null);
        FlattenResult<MSCEmployeeEvent> result = MSCEventMapper.validateAndFlatten(req);
        assertThat(result.isOk()).isFalse();
        assertThat(result.getErrors())
                .anySatisfy(e -> {
                    assertThat(e.getPath()).isEqualTo("formCSMEvents[0].header");
                    assertThat(e.getMessage()).contains("mandatory");
                });
    }

    @Test
    void validateAndFlatten_missing_eventData_errors() {
        FormCSMEventsRequest req = validRequest();
        req.getFormCSMEvents().get(0).setEventData(null);
        FlattenResult<MSCEmployeeEvent> result = MSCEventMapper.validateAndFlatten(req);
        assertThat(result.isOk()).isFalse();
        assertThat(result.getErrors())
                .anySatisfy(e -> assertThat(e.getPath()).isEqualTo("formCSMEvents[0].eventData"));
    }

    @Test
    void validateAndFlatten_empty_employees_errors() {
        FormCSMEventsRequest req = validRequest();
        req.getFormCSMEvents().get(0).getEventData().setEmployees(Collections.emptyList());
        FlattenResult<MSCEmployeeEvent> result = MSCEventMapper.validateAndFlatten(req);
        assertThat(result.isOk()).isFalse();
        assertThat(result.getErrors())
                .anySatisfy(e -> assertThat(e.getPath()).isEqualTo("formCSMEvents[0].eventData.employees"));
    }

    @Test
    void validateAndFlatten_missing_empId_sets_error_and_row_present() {
        FormCSMEventsRequest req = validRequest();
        req.getFormCSMEvents().get(0).getEventData().getEmployees().get(0).setEmpId(null);
        FlattenResult<MSCEmployeeEvent> result = MSCEventMapper.validateAndFlatten(req);
        assertThat(result.isOk()).isFalse();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getErrors())
                .anySatisfy(e -> {
                    assertThat(e.getPath()).isEqualTo("formCSMEvents[0].eventData.employees[0].empId");
                    assertThat(e.getMessage()).contains("mandatory");
                });
    }
}
