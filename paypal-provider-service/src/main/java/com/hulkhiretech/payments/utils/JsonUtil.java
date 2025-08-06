package com.hulkhiretech.payments.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    // ✅ Convert JSON string to JsonNode
    public static JsonNode stringToJson(String jsonString) {
        try {
            return mapper.readTree(jsonString);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON string", e);
        }
    }

    // ✅ Convert Object to JSON string
    public static String objectToJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Unable to convert object to JSON string", e);
        }
    }

    // ✅ Convert String or JSON-compatible object to POJO
    public static <T> T fromJson(Object input, Class<T> class1) {
        try {
            if (input instanceof String) {
                return mapper.readValue((String) input, class1);
            } else {
                String json = objectToJson(input); // Reuse your existing method
                return mapper.readValue(json, class1);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to convert to class: " + class1.getSimpleName(), e);
        }
    }
}
