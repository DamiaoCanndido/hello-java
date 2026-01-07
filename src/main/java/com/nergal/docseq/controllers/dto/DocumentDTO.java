package com.nergal.docseq.controllers.dto;

import java.util.List;

public record DocumentDTO(List<DocumentItemDTO> documents, 
                          int page, 
                          int pageSize, 
                          int totalPages, 
                          long totalElements) {

}
