package com.eyedia.eyedia.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
        int page,
        int limit,
        int totalPages,
        long totalElements,
        boolean hasNext,
        List<T> items
) {
    public static <T> PageResponse<T> from(Page<T> p) {
        return new PageResponse<>(
                p.getNumber(),
                p.getSize(),
                p.getTotalPages(),
                p.getTotalElements(),
                p.hasNext(),
                p.getContent()
        );
    }
}
