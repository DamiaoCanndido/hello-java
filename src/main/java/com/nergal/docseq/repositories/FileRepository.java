package com.nergal.docseq.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nergal.docseq.entities.File;
import com.nergal.docseq.entities.Folder;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {

    // List files in a folder
    List<File> findByFolderFolderIdAndDeletedAtIsNull(
        UUID folderId
    );

    // List files in the root directory.
    List<File> findByFolderIsNullAndTownshipTownshipIdAndDeletedAtIsNull(
        UUID townshipId
    );

    // Search for favorite files
    List<File> findByTownshipTownshipIdAndFavoriteTrueAndDeletedAtIsNull(
        UUID townshipId
    );

    // Recycle Bin – Deleted Files
    List<File> findByTownshipTownshipIdAndDeletedAtIsNotNull(
        UUID townshipId
    );

    // Search for files securely
    Optional<File> findByFileIdAndTownshipTownshipIdAndDeletedAtIsNull(
        UUID fileId,
        UUID townshipId
    );

    // Check for duplicate names in the same folder
    boolean existsByNameAndFolderAndDeletedAtIsNull(
        String name,
        Folder folder
    );

    // Search for restore
    Optional<File> findByFileIdAndDeletedAtIsNotNull(UUID fileId);
}
