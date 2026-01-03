package com.nergal.docseq.controllers.dto;

import java.util.List;

public record PermissionDTO(
    List<PermissionItemDTO> permissions, 
    int page, 
    int pageSize, 
    int totalPages, 
    long totalElements) {
}
