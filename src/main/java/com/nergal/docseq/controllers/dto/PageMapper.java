package com.nergal.docseq.controllers.dto;

import org.springframework.data.domain.Page;

public final class PageMapper {

    private PageMapper() {}

    public static <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }
}

