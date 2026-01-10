package com.nergal.docseq.services;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nergal.docseq.entities.Folder;
import com.nergal.docseq.entities.FolderPermission;
import com.nergal.docseq.entities.FolderPermission.FolderPermissionType;
import com.nergal.docseq.entities.User;
import com.nergal.docseq.exception.NotFoundException;
import com.nergal.docseq.repositories.FolderPermissionRepository;
import com.nergal.docseq.repositories.FolderRepository;
import com.nergal.docseq.repositories.UserRepository;

@Service
public class FolderPermissionService {

    private final FolderPermissionRepository repository;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;

    public FolderPermissionService(
        FolderPermissionRepository repository, 
        UserRepository userRepository,
        FolderRepository folderRepository) {
            this.repository = repository;
            this.userRepository = userRepository;
            this.folderRepository = folderRepository;
    }

    public void check(
        UUID ownerUserId,
        UUID folderId,
        FolderPermissionType permission
    ) {
        boolean allowed = repository
            .existsByUserUserIdAndFolderFolderIdAndPermission(
                ownerUserId,
                folderId,
                permission
            );

        if (!allowed) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Permission denied"
            );
        }
    }

    @Transactional
    public void grantCascade(
        UUID ownerUserId,
        UUID targetUserId,
        UUID folderId,
        FolderPermissionType permission
    ) {

        Folder root = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Folder not found"
                ));

        validateOwnership(ownerUserId, root);

        User targetUser = userRepository.getReferenceById(targetUserId);

        grantRecursively(targetUser, root, permission);
    }

    private void grantRecursively(
        User targetUser,
        Folder folder,
        FolderPermissionType permission
    ) {

        boolean exists =
            repository.existsByUserUserIdAndFolderFolderIdAndPermission(
                    targetUser.getUserId(),
                    folder.getFolderId(),
                    permission
            );

        if (!exists) {
            FolderPermission fp = new FolderPermission();
            fp.setUser(targetUser);
            fp.setFolder(folder);
            fp.setPermission(permission);

            repository.save(fp);
        }

        List<Folder> children =
            folderRepository.findByParentFolderIdAndDeletedAtIsNull(
                    folder.getFolderId()
            );

        for (Folder child : children) {
            grantRecursively(targetUser, child, permission);
        }
    }

    @Transactional
    public void revokeCascade(
        UUID ownerUserId,
        UUID targetUserId,
        UUID folderId,
        FolderPermissionType permission
    ) {

        Folder root = folderRepository.findById(folderId)
            .orElseThrow(() -> new NotFoundException(
                    "Folder not found"
            ));

        validateOwnership(ownerUserId, root);

        revokeRecursively(targetUserId, root, permission);
    }

    private void revokeRecursively(
        UUID targetUserId,
        Folder folder,
        FolderPermissionType permission
    ) {

        repository.deleteByUserUserIdAndFolderFolderIdAndPermission(
            targetUserId,
            folder.getFolderId(),
            permission
        );

        List<Folder> children =
            folderRepository.
            findByParentFolderIdAndDeletedAtIsNull(folder.getFolderId());

        for (Folder child : children) {
            revokeRecursively(targetUserId, child, permission);
        }
    }

    private void validateOwnership(UUID ownerUserId, Folder folder) {
        if (!folder.getCreatedBy().getUserId().equals(ownerUserId)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Only the folder owner can manage permissions"
            );
        }
    }
}

