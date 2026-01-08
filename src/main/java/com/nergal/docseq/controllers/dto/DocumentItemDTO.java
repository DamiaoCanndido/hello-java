package com.nergal.docseq.controllers.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentItemDTO(
    UUID id, 
    String description, 
    Integer order, 
    LocalDateTime createdAt,
    String createdBy
) { }
