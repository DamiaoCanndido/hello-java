package com.nergal.docseq.controllers.dto;

import java.util.List;

public record TownshipDTO(List<TownshipItemDTO> townships, 
                          int page, 
                          int pageSize, 
                          int totalPages, 
                          long totalElements) {

}
