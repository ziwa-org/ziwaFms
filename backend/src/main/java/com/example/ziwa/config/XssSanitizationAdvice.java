package com.example.ziwa.config;

import com.example.ziwa.util.XssSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Controller advice that automatically sanitizes all text inputs in request bodies
 * to prevent XSS attacks.
 */
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class XssSanitizationAdvice extends RequestBodyAdviceAdapter {

    private final XssSanitizer xssSanitizer;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                          Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // Apply to all request bodies
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
                               MethodParameter parameter, Type targetType,
                               Class<? extends HttpMessageConverter<?>> converterType) {
        sanitizeObject(body);
        return body;
    }

    /**
     * Recursively sanitizes all String fields in an object.
     */
    private void sanitizeObject(Object obj) {
        if (obj == null) {
            return;
        }

        Class<?> clazz = obj.getClass();
        
        // Skip primitive types and common immutable types
        if (clazz.isPrimitive() || clazz.getName().startsWith("java.lang") 
            || clazz.getName().startsWith("java.time")) {
            return;
        }

        // Process all fields
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (value == null) {
                    continue;
                }

                // Sanitize String fields
                if (value instanceof String) {
                    String sanitized = xssSanitizer.sanitize((String) value);
                    if (!sanitized.equals(value)) {
                        log.debug("Sanitized field '{}' in class '{}'", field.getName(), clazz.getSimpleName());
                    }
                    field.set(obj, sanitized);
                }
                // Recursively sanitize nested objects
                else if (!value.getClass().isPrimitive() 
                         && !value.getClass().getName().startsWith("java.lang")
                         && !value.getClass().getName().startsWith("java.time")) {
                    sanitizeObject(value);
                }
            } catch (IllegalAccessException e) {
                log.warn("Could not access field '{}' for sanitization", field.getName(), e);
            }
        }
    }
}
