package com.example.msc.service;

import com.example.msc.model.EventHeader;
import com.example.msc.model.FormMSCEvent;
import com.example.msc.model.FormMSCEventsRequest;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MscEventServiceTest {

    private MscEventService service;

    @BeforeEach
    void setUp() {
        service = new MscEventService();
    }

    @ParameterizedTest
    @MethodSource("dynamicPayloadProvider")
    void processEvents_traversesMultipleDynamicPayloads(
            Map<String, Object> dynamicAdditionalProps,
            List<String> expectedLeafPathFragments
    ) {
        FormMSCEventsRequest request = buildRequestWithDynamicAdditionalProps(dynamicAdditionalProps);

        Logger logger = (Logger) LoggerFactory.getLogger(MscEventService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        service.processEvents(request);

        List<String> leafMessages = listAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .filter(msg -> msg.startsWith("Leaf at"))
                .collect(Collectors.toList());

        expectedLeafPathFragments.forEach(expectedPathFragment ->
                assertThat(leafMessages)
                        .anySatisfy(msg ->
                                assertThat(msg)
                                        .as("Expect log to contain path fragment '%s'", expectedPathFragment)
                                        .contains(expectedPathFragment)
                        )
        );
    }

    @ParameterizedTest
    @MethodSource("headerValidityProvider")
    void processEvents_headerValidityControlsTraversal(
            String eventStatus,
            String delChanId,
            boolean expectTraversal
    ) {
        FormMSCEventsRequest request = new FormMSCEventsRequest();

        EventHeader header = new EventHeader();
        header.setEventStatus(eventStatus);
        header.setDelChanId(delChanId);

        FormMSCEvent event = new FormMSCEvent();
        event.setHeader(header);
        event.setAdditionalProperty("foo", "bar");

        request.setFormMSCEvents(Collections.singletonList(event));

        Logger logger = (Logger) LoggerFactory.getLogger(MscEventService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        service.processEvents(request);

        long leafCount = listAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .filter(msg -> msg.startsWith("Leaf at"))
                .count();

        if (expectTraversal) {
            assertThat(leafCount)
                    .as("Expected traversal (leaf logs) for valid header")
                    .isGreaterThan(0);
        } else {
            assertThat(leafCount)
                    .as("Expected NO traversal (no leaf logs) for invalid header")
                    .isZero();

            boolean skippedLogged = listAppender.list.stream()
                    .map(ILoggingEvent::getFormattedMessage)
                    .anyMatch(msg -> msg.contains("Skipping event with invalid header"));

            assertThat(skippedLogged).isTrue();
        }
    }

    static Stream<Arguments> dynamicPayloadProvider() {
        Map<String, Object> emp1 = new HashMap<>();
        emp1.put("id", 1);
        emp1.put("name", "Alice");

        Map<String, Object> emp2 = new HashMap<>();
        emp2.put("id", 2);
        emp2.put("name", "Bob");

        List<Map<String, Object>> employees = Arrays.asList(emp1, emp2);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("employees", employees);
        eventData.put("flag", true);

        Map<String, Object> dynamic1 = new HashMap<>();
        dynamic1.put("eventData", eventData);
        dynamic1.put("correlationId", "run-123");

        List<String> expectedPaths1 = Arrays.asList(
                "eventData.employees[0].id",
                "eventData.employees[0].name",
                "eventData.employees[1].id",
                "eventData.employees[1].name",
                "eventData.flag",
                "correlationId"
        );

        Map<String, Object> dynamic2 = new HashMap<>();
        dynamic2.put("correlationId", "xyz-999");
        dynamic2.put("tenantId", "tenant-1");
        dynamic2.put("retryCount", 3);

        List<String> expectedPaths2 = Arrays.asList(
                "correlationId",
                "tenantId",
                "retryCount"
        );

        Map<String, Object> dynamic3 = new HashMap<>();
        dynamic3.put("tags", Arrays.asList("A", "B", "C"));

        List<String> expectedPaths3 = Arrays.asList(
                "tags[0]",
                "tags[1]",
                "tags[2]"
        );

        return Stream.of(
                Arguments.of(dynamic1, expectedPaths1),
                Arguments.of(dynamic2, expectedPaths2),
                Arguments.of(dynamic3, expectedPaths3)
        );
    }

    static Stream<Arguments> headerValidityProvider() {
        return Stream.of(
                Arguments.of("abc", "1234", true),
                Arguments.of("   ", "1234", false),
                Arguments.of("abc", "   ", false),
                Arguments.of("   ", "   ", false)
        );
    }

    private FormMSCEventsRequest buildRequestWithDynamicAdditionalProps(Map<String, Object> dynamicProps) {
        FormMSCEventsRequest request = new FormMSCEventsRequest();

        EventHeader header = new EventHeader();
        header.setEventStatus("abc");
        header.setDelChanId("1234");

        FormMSCEvent event = new FormMSCEvent();
        event.setHeader(header);

        if (dynamicProps != null) {
            dynamicProps.forEach(event::setAdditionalProperty);
        }

        request.setFormMSCEvents(Collections.singletonList(event));
        return request;
    }
}
