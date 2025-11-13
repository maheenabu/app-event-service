package com.example.msc.util;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public final class JsonTraversalUtil {

    private JsonTraversalUtil() {}

    @SuppressWarnings("unchecked")
    public static void traverse(Object value, BiConsumer<String, Object> visitor) {
        traverse("", value, visitor);
    }

    @SuppressWarnings("unchecked")
    private static void traverse(String path, Object value, BiConsumer<String, Object> visitor) {

        if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;

            map.entrySet().forEach(entry -> {
                String nextPath = path.isEmpty()
                        ? entry.getKey()
                        : path + "." + entry.getKey();
                traverse(nextPath, entry.getValue(), visitor);
            });

        } else if (value instanceof List) {
            List<?> list = (List<?>) value;

            IntStream.range(0, list.size())
                    .forEach(i -> {
                        String nextPath = path + "[" + i + "]";
                        traverse(nextPath, list.get(i), visitor);
                    });

        } else {
            visitor.accept(path, value);
        }
    }

    public static Map<String, Object> flatten(Object value) {
        Map<String, Object> result = new LinkedHashMap<>();
        traverse(value, result::put);
        return result;
    }
}
