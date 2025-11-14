package com.example.msc.service.processing;

import java.util.*;

@SuppressWarnings("unchecked")
public class DynamicExtractor {

    public static List<Map<String, String>> extractEmployees(Map<String, Object> dynamicBody) {

        Object eventDataObj = dynamicBody.get("eventData");
        if (!(eventDataObj instanceof Map)) return Collections.emptyList();

        Map<String, Object> eventData = (Map<String, Object>) eventDataObj;
        Object employeesObj = eventData.get("employees");

        return convertToListOfStringMaps(employeesObj);
    }

    public static List<Map<String, String>> extractCustomers(Map<String, Object> dynamicBody) {
        Object customersObj = dynamicBody.get("Customers");
        return convertToListOfStringMaps(customersObj);
    }

    public static Map<String, String> extractEventDetailsData(Map<String, Object> dynamicBody) {

        Object eventDataObj = dynamicBody.get("eventData");
        if (!(eventDataObj instanceof Map)) return Collections.emptyMap();

        Map<String, Object> eventData = (Map<String, Object>) eventDataObj;
        Object detailsObj = eventData.get("eventDetailsData");
        if (!(detailsObj instanceof Map)) return Collections.emptyMap();

        Map<?, ?> raw = (Map<?, ?>) detailsObj;
        Map<String, String> result = new LinkedHashMap<>();

        raw.forEach((key, value) ->
                result.put(String.valueOf(key), value == null ? null : String.valueOf(value))
        );

        return result;
    }

    private static List<Map<String, String>> convertToListOfStringMaps(Object obj) {
        if (!(obj instanceof List)) return Collections.emptyList();

        List<?> list = (List<?>) obj;
        List<Map<String, String>> result = new ArrayList<>();

        for (Object item : list) {
            if (item instanceof Map) {
                Map<?, ?> raw = (Map<?, ?>) item;
                Map<String, String> converted = new LinkedHashMap<>();

                raw.forEach((key, value) ->
                        converted.put(String.valueOf(key),
                                      value == null ? null : String.valueOf(value))
                );

                result.add(converted);
            }
        }
        return result;
    }
}
