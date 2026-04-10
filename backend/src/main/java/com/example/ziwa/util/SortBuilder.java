package com.example.ziwa.util;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility for building Sort instances
 * Requirements: 15.4
 */
public class SortBuilder {
    
    /**
     * Build a Sort from field and direction
     */
    public static Sort build(String field, String direction) {
        if (field == null || field.trim().isEmpty()) {
            return Sort.unsorted();
        }
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        
        return Sort.by(sortDirection, field);
    }
    
    /**
     * Build a Sort from multiple fields with the same direction
     */
    public static Sort build(List<String> fields, String direction) {
        if (fields == null || fields.isEmpty()) {
            return Sort.unsorted();
        }
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        
        List<Sort.Order> orders = new ArrayList<>();
        for (String field : fields) {
            if (field != null && !field.trim().isEmpty()) {
                orders.add(new Sort.Order(sortDirection, field));
            }
        }
        
        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }
    
    /**
     * Build a Sort from field-direction pairs
     * Example: buildMulti(List.of("name:asc", "date:desc"))
     */
    public static Sort buildMulti(List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return Sort.unsorted();
        }
        
        List<Sort.Order> orders = new ArrayList<>();
        for (String param : sortParams) {
            String[] parts = param.split(":");
            if (parts.length >= 1 && !parts[0].trim().isEmpty()) {
                String field = parts[0].trim();
                Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
                orders.add(new Sort.Order(direction, field));
            }
        }
        
        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }
}
