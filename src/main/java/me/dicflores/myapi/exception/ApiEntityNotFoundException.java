package me.dicflores.myapi.exception;

import java.util.HashMap;
import java.util.Map;

public class ApiEntityNotFoundException extends Exception {
    public ApiEntityNotFoundException(Class clazz, String... searchParamsMap) {
        super(buildMessage(clazz.getSimpleName(), toMap(searchParamsMap)));
    }

    private static String buildMessage(String entity, Map<String, String> searchParams) {
        return String.format("%s was not found for parameters %s", entity, searchParams);
    }

    private static Map<String, String> toMap(String... entries) {
        if (entries.length % 2 == 1) {
            throw new IllegalArgumentException("Invalid entries");
        }
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < entries.length / 2; i = i + 2) {
            map.put(entries[i], entries[i+1]);
        }
        return map;
    }
}
