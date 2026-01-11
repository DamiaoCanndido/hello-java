package com.nergal.docseq.controllers.dto.files;

import org.springframework.core.io.Resource;

public record FileStream(
    Resource resource,
    String contentType,
    String filename
) {}