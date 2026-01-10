package com.nergal.docseq.controllers.dto.permissions;

import java.util.UUID;

import com.nergal.docseq.entities.FolderPermission.FolderPermissionType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FolderPermissionsRequestDTO(

    @NotBlank(message = "user id is required")
    UUID targetUserId,

    @NotBlank(message = "folder id is required")
    UUID folderId,

    @Enumerated(EnumType.STRING)
    @NotNull(message = "permission is required")
    FolderPermissionType permission
) { }
