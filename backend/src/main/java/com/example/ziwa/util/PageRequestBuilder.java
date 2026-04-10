package com.example.ziwa.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility for building Pageable instances with pagination and sorting
 * Requirements: 15.1, 15.4, 15.5
 */
public class PageRequestBuilder {
    
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    
    /**
     * Build a Pageable with page number and size
     * Requirements: 15.1, 15.5
     */
    public static Pageable build(Integer page, Integer size) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        int pageSize = size != null && size > 0 ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        
        return PageRequest.of(pageNumber, pageSize);
    }
    
    /**
     * Build a Pageable with page number, size, and sorting
     * Requirements: 15.1, 15.4, 15.5
     */
    public static Pageable build(Integer page, Integer size, String sortBy, String sortDirection) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        int pageSize = size != null && size > 0 ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
            Sort sort = Sort.by(direction, sortBy);
            return PageRequest.of(pageNumber, pageSize, sort);
        }
        
        return PageRequest.of(pageNumber, pageSize);
    }
    
    /**
     * Build a Pageable with Sort object
     * Requirements: 15.1, 15.4
     */
    public static Pageable build(Integer page, Integer size, Sort sort) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        int pageSize = size != null && size > 0 ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        
        if (sort != null && sort.isSorted()) {
            return PageRequest.of(pageNumber, pageSize, sort);
        }
        
        return PageRequest.of(pageNumber, pageSize);
    }
    
    /**
     * Get the default page size
     */
    public static int getDefaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }
    
    /**
     * Get the maximum page size
     */
    public static int getMaxPageSize() {
        return MAX_PAGE_SIZE;
    }
}
