package com.nergal.docseq.controllers.dto.folders;

import com.nergal.docseq.controllers.dto.PageResponse;
import com.nergal.docseq.controllers.dto.files.FileResponseDTO;

public record FolderContentResponse(
    PageResponse<FolderResponseDTO> folders,
    PageResponse<FileResponseDTO> files
) { }
