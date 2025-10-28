package com.example.LuckyWheel.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Slf4j
public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T parseJson(Resource resource, Class<T> clazz) {
        try {
            return mapper.readValue(resource.getInputStream(), clazz);
        } catch (IOException e) {
            log.error("Error parsing JSON to class: {}", clazz.getName(), e);
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    public static <T> T parseJson(Resource resource, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(resource.getInputStream(), typeReference);
        } catch (IOException e) {
            log.error("Error parsing JSON with TypeReference", e);
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }
}
