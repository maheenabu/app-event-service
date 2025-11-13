package com.example.msc.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class JsonTraversalUtilTest {

    @ParameterizedTest
    @MethodSource("dynamicJsonProvider")
    void testFlattenDynamicJson(Object dynamicJson, Map<String, Object> expectedFlat) {

        Map<String, Object> actual = JsonTraversalUtil.flatten(dynamicJson);

        assertThat(actual)
                .containsAllEntriesOf(expectedFlat)
                .hasSize(expectedFlat.size());
    }

    static List<Object[]> dynamicJsonProvider() {

        // CASE 1: nested employees
        Map<String, Object> emp1 = new HashMap<>();
        emp1.put("id", 1);
        emp1.put("name", "Alice");

        Map<String, Object> emp2 = new HashMap<>();
        emp2.put("id", 2);
        emp2.put("name", "Bob");

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("employees", Arrays.asList(emp1, emp2));

        Map<String, Object> json1 = new HashMap<>();
        json1.put("eventData", eventData);
        json1.put("flag", true);

        Map<String, Object> flat1 = new LinkedHashMap<>();
        flat1.put("eventData.employees[0].id", 1);
        flat1.put("eventData.employees[0].name", "Alice");
        flat1.put("eventData.employees[1].id", 2);
        flat1.put("eventData.employees[1].name", "Bob");
        flat1.put("flag", true);

        // CASE 2: list of primitives
        Map<String, Object> json2 = new HashMap<>();
        json2.put("tags", Arrays.asList("A", "B", "C"));

        Map<String, Object> flat2 = new LinkedHashMap<>();
        flat2.put("tags[0]", "A");
        flat2.put("tags[1]", "B");
        flat2.put("tags[2]", "C");

        // CASE 3: flat map
        Map<String, Object> json3 = new HashMap<>();
        json3.put("foo", "bar");
        json3.put("num", 42);

        Map<String, Object> flat3 = new LinkedHashMap<>();
        flat3.put("foo", "bar");
        flat3.put("num", 42);

        return Arrays.asList(
                new Object[]{json1, flat1},
                new Object[]{json2, flat2},
                new Object[]{json3, flat3}
        );
    }
}
