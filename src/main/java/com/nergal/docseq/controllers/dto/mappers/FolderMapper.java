package com.nergal.docseq.controllers.dto.mappers;

import com.nergal.docseq.controllers.dto.folders.FolderResponseDTO;
import com.nergal.docseq.entities.Folder;

public class FolderMapper {

    private FolderMapper() {}

    public static FolderResponseDTO toDTO(Folder folder) {
        return new FolderResponseDTO(
                folder.getFolderId(),
                folder.getName(),
                folder.getParent() != null
                        ? folder.getParent().getFolderId()
                        : null,
                folder.getFavorite(),
                folder.getCreatedAt(),
                folder.getUpdatedAt()
        );
    }
}

