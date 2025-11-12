package com.example.csm.app.mapper;

import com.example.csm.app.dto.MSCEmployeeEvent;
import com.example.csm.app.validation.FlattenResult;
import com.example.csm.app.validation.ValidationError;
import com.example.csm.model.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class MSCEventMapper {

    private MSCEventMapper() {}

    public static FlattenResult<MSCEmployeeEvent> validateAndFlatten(FormCSMEventsRequest root) {
        final List<ValidationError> errors = new ArrayList<>();

        final List<FormCSMEvent> events = Optional.ofNullable(root)
                .map(FormCSMEventsRequest::getFormCSMEvents)
                .orElse(null);

        if (events == null || events.isEmpty()) {
            errors.add(new ValidationError("formCSMEvents", "is required and must not be empty"));
            return FlattenResult.of(Collections.emptyList(), errors);
        }

        final List<MSCEmployeeEvent> items =
                IntStream.range(0, events.size())
                        .mapToObj(idx -> flattenEvent(events.get(idx), idx, errors))
                        .filter(Objects::nonNull)
                        .flatMap(List::stream)
                        .collect(Collectors.toList());

        return FlattenResult.of(items, errors);
    }

    private static List<MSCEmployeeEvent> flattenEvent(FormCSMEvent evt, int evtIdx, List<ValidationError> errors) {
        final String base = "formCSMEvents[" + evtIdx + "]";

        final Header header = Optional.ofNullable(evt.getHeader())
                .orElseGet(() -> { errors.add(new ValidationError(base + ".header", "is mandatory")); return null; });

        final EventData data = Optional.ofNullable(evt.getEventData())
                .orElseGet(() -> { errors.add(new ValidationError(base + ".eventData", "is mandatory")); return null; });

        if (header == null || data == null) return null;

        final List<Employee> employees = Optional.ofNullable(data.getEmployees()).orElse(null);
        if (employees == null || employees.isEmpty()) {
            errors.add(new ValidationError(base + ".eventData.employees", "list is mandatory and must not be empty"));
            return null;
        }

        final EventDetailData details = Optional.ofNullable(data.getEventDetailData()).orElse(null);

        return IntStream.range(0, employees.size())
                .mapToObj(i -> {
                    final Employee emp = employees.get(i);
                    final String empPath = base + ".eventData.employees[" + i + "]";

                    final String empId = Optional.ofNullable(emp.getEmpId()).orElse("");
                    if (empId.isEmpty()) {
                        errors.add(new ValidationError(empPath + ".empId", "is mandatory and must not be empty"));
                    }

                    return MSCEmployeeEvent.builder()
                            .eventStatus(nz(header.getEventStatus()))
                            .deliveryChannelId(nz(header.getDeliveryChannelId()))
                            .svpSessionId(details == null ? "" : nz(details.getSvpSessionId()))
                            .formId(details == null ? "" : nz(details.getFormId()))
                            .empId(empId)
                            .extras(Optional.ofNullable(emp.getAdditionalProperties()).orElse(Collections.emptyMap()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private static String nz(String s) { return s == null ? "" : s; }
}
