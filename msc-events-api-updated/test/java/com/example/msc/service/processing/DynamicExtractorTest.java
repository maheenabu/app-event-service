package com.example.msc.service.processing;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DynamicExtractorTest {

    @Test
    void extractEmployees_happyPath_singleEmployee() {
        Map<String, Object> employee = new HashMap<>();
        employee.put("id", 1);
        employee.put("name", "Alice");

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("employees", Collections.singletonList(employee));

        Map<String, Object> root = new HashMap<>();
        root.put("eventData", eventData);

        List<Map<String, String>> employees = DynamicExtractor.extractEmployees(root);

        assertEquals(1, employees.size());
        Map<String, String> e0 = employees.get(0);
        assertEquals("1", e0.get("id"));
        assertEquals("Alice", e0.get("name"));
    }

    @Test
    void extractCustomers_happyPath_singleCustomer() {
        Map<String, Object> customer = new HashMap<>();
        customer.put("id", 1);
        customer.put("name", "Alice");

        Map<String, Object> root = new HashMap<>();
        root.put("Customers", Collections.singletonList(customer));

        List<Map<String, String>> customers = DynamicExtractor.extractCustomers(root);

        assertEquals(1, customers.size());
        Map<String, String> c0 = customers.get(0);
        assertEquals("1", c0.get("id"));
        assertEquals("Alice", c0.get("name"));
    }

    @Test
    void extractEventDetailsData_happyPath() {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("svpSessionId", "abc");
        details.put("formId", "sxx234");

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventDetailsData", details);

        Map<String, Object> root = new HashMap<>();
        root.put("eventData", eventData);

        Map<String, String> result = DynamicExtractor.extractEventDetailsData(root);

        assertEquals(2, result.size());
        assertEquals("abc", result.get("svpSessionId"));
        assertEquals("sxx234", result.get("formId"));
    }

    // -------- Negative / invalid JSON shape tests --------

    @Test
    void extractEmployees_missingEventData_returnsEmptyList() {
        Map<String, Object> root = new HashMap<>();
        List<Map<String, String>> employees = DynamicExtractor.extractEmployees(root);
        assertTrue(employees.isEmpty());
    }

    @Test
    void extractEmployees_eventDataNotMap_returnsEmptyList() {
        Map<String, Object> root = new HashMap<>();
        root.put("eventData", "not-a-map");
        List<Map<String, String>> employees = DynamicExtractor.extractEmployees(root);
        assertTrue(employees.isEmpty());
    }

    @Test
    void extractEmployees_employeesNotList_returnsEmptyList() {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("employees", "not-a-list");

        Map<String, Object> root = new HashMap<>();
        root.put("eventData", eventData);

        List<Map<String, String>> employees = DynamicExtractor.extractEmployees(root);
        assertTrue(employees.isEmpty());
    }

    @Test
    void extractEmployees_listItemsNotMaps_ignoresInvalidEntries() {
        Map<String, Object> good = new HashMap<>();
        good.put("id", 1);
        good.put("name", "Alice");

        List<Object> list = new ArrayList<>();
        list.add("bad-item");
        list.add(good);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("employees", list);

        Map<String, Object> root = new HashMap<>();
        root.put("eventData", eventData);

        List<Map<String, String>> employees = DynamicExtractor.extractEmployees(root);
        assertEquals(1, employees.size());
        assertEquals("Alice", employees.get(0).get("name"));
    }

    @Test
    void extractCustomers_customersNotList_returnsEmptyList() {
        Map<String, Object> root = new HashMap<>();
        root.put("Customers", "not-a-list");
        List<Map<String, String>> customers = DynamicExtractor.extractCustomers(root);
        assertTrue(customers.isEmpty());
    }

    @Test
    void extractEventDetailsData_missingEventData_returnsEmptyMap() {
        Map<String, Object> root = new HashMap<>();
        Map<String, String> result = DynamicExtractor.extractEventDetailsData(root);
        assertTrue(result.isEmpty());
    }

    @Test
    void extractEventDetailsData_eventDetailsNotMap_returnsEmptyMap() {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventDetailsData", "not-a-map");

        Map<String, Object> root = new HashMap<>();
        root.put("eventData", eventData);

        Map<String, String> result = DynamicExtractor.extractEventDetailsData(root);
        assertTrue(result.isEmpty());
    }
}
