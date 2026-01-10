package com.nergal.docseq.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nergal.docseq.controllers.dto.PageResponse;
import com.nergal.docseq.controllers.dto.folders.CreateFolderRequestDTO;
import com.nergal.docseq.controllers.dto.folders.FolderResponseDTO;
import com.nergal.docseq.controllers.dto.folders.FolderTreeResponseDTO;
import com.nergal.docseq.controllers.dto.folders.UpdateFolderRequestDTO;
import com.nergal.docseq.controllers.dto.mappers.FolderMapper;
import com.nergal.docseq.controllers.dto.mappers.FolderTreeBuilder;
import com.nergal.docseq.controllers.dto.mappers.PageMapper;
import com.nergal.docseq.entities.File;
import com.nergal.docseq.entities.Folder;
import com.nergal.docseq.entities.User;
import com.nergal.docseq.exception.ConflictException;
import com.nergal.docseq.exception.NotFoundException;
import com.nergal.docseq.repositories.FileRepository;
import com.nergal.docseq.repositories.FolderRepository;
import com.nergal.docseq.repositories.UserRepository;

@Service
public class FolderService {
    
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public FolderService(
        FolderRepository folderRepository, 
        FileRepository fileRepository,
        UserRepository userRepository
    ) {
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    // List root folders
    @Transactional(readOnly = true)
    public PageResponse<FolderResponseDTO> listRootFolders(
            Pageable pageable,
            JwtAuthenticationToken token
    ) {
        var township_id = getTownshipId(token);

        var page = folderRepository
                .findByTownshipTownshipIdAndParentIsNullAndDeletedAtIsNull(
                        township_id,
                        pageable
                )
                .map(FolderMapper::toDTO);

        return PageMapper.toPageResponse(page);
    }

    // List subfolders
    @Transactional(readOnly = true)
    public PageResponse<FolderResponseDTO> listChildren(
            UUID parentId,
            Pageable pageable,
            JwtAuthenticationToken token
    ) {
        var township_id = getTownshipId(token);

        folderRepository.findByFolderIdAndTownshipTownshipIdAndDeletedAtIsNull(
                parentId, township_id)
                .orElseThrow(() -> new NotFoundException("folder not found"));

        var page = folderRepository
                .findByParentFolderIdAndDeletedAtIsNull(parentId, pageable)
                .map(FolderMapper::toDTO);

        return PageMapper.toPageResponse(page);
    }

    // Complete tree
    @Transactional(readOnly = true)
    public List<FolderTreeResponseDTO> getFolderTree(
            JwtAuthenticationToken token
    ) {
        var townshipId = getTownshipId(token);

        var folders = folderRepository
                .findByTownshipTownshipIdAndDeletedAtIsNull(townshipId);

        return FolderTreeBuilder.build(folders);
    }

    // Create folder
    @Transactional
    public void create(CreateFolderRequestDTO dto, JwtAuthenticationToken token) {
        var user = getUser(token);

        Folder parent = null;
        if (dto.parentId() != null) {
            parent = folderRepository.findByFolderIdAndTownshipTownshipIdAndDeletedAtIsNull(
                    dto.parentId(),
                    user.getTownship().getTownshipId()
            ).orElseThrow(() -> new NotFoundException("parent folder not found"));
        }

        if (folderRepository.existsByNameAndParentAndDeletedAtIsNull(dto.name(), parent)) {
            throw new ConflictException("Folder already exists");
        }

        Folder folder = new Folder();
        folder.setName(dto.name());
        folder.setParent(parent);
        folder.setTownship(user.getTownship());
        folder.setCreatedBy(user);

        folderRepository.save(folder);
    }

    // Update folder
    @Transactional
    public void update(
            UUID folderId,
            UpdateFolderRequestDTO dto,
            JwtAuthenticationToken token
    ) {
        var user = getUser(token);

        Folder folder = folderRepository
                .findByFolderIdAndTownshipTownshipIdAndDeletedAtIsNull(
                        folderId,
                        user.getTownship().getTownshipId()
                )
                .orElseThrow(() -> new NotFoundException("folder not found"));

        if (dto.name() != null &&
            folderRepository.existsByNameAndParentAndDeletedAtIsNull(dto.name(), folder.getParent())) {
            throw new ConflictException("Folder already exists");
        }

        if (dto.name() != null) folder.setName(dto.name());
        if (dto.favorite() != null) folder.setFavorite(dto.favorite());
    }

    // Soft delete
    @Transactional
    public void delete(UUID folderId, JwtAuthenticationToken token) {
        var user = getUser(token);

        Folder folder = folderRepository
                .findByFolderIdAndTownshipTownshipIdAndDeletedAtIsNull(
                        folderId,
                        user.getTownship().getTownshipId()
                )
                .orElseThrow(() -> new NotFoundException("folder not found"));

        softDeleteRecursively(folder, user);
    }

    @Transactional
    public void softDeleteRecursively(Folder folder, User deletedBy) {

        folder.setDeletedAt(LocalDateTime.now().toInstant(null));
        folder.setDeletedBy(deletedBy);

        List<File> files = fileRepository.findByFolderAndDeletedAtIsNull(folder);
        for (File file : files) {
            file.setDeletedAt(LocalDateTime.now().toInstant(null));
            file.setDeletedBy(deletedBy);
        }

        List<Folder> children = folderRepository.findByParentAndDeletedAtIsNull(folder);
        for (Folder child : children) {
            softDeleteRecursively(child, deletedBy);
        }
    }

    // List trash can
    @Transactional(readOnly = true)
    public PageResponse<FolderResponseDTO> listTrash(
            Pageable pageable,
            JwtAuthenticationToken token
    ) {
        var townshipId = getTownshipId(token);

        var page = folderRepository
                .findByTownshipTownshipIdAndDeletedAtIsNotNull(
                        townshipId,
                        pageable
                )
                .map(FolderMapper::toDTO);

        return PageMapper.toPageResponse(page);
    }

    // Restore folder with children
    @Transactional
    public void restore(UUID folderId, JwtAuthenticationToken token) {

        Folder folder = folderRepository
                .findByFolderIdAndDeletedAtIsNotNull(folderId)
                .orElseThrow(() -> new NotFoundException("folder not found"));

        restoreRecursively(folder);
    }

    private void restoreRecursively(Folder folder) {
        folder.setDeletedAt(null);
        folder.setDeletedBy(null);

        folder.getChildren().forEach(this::restoreRecursively);
        fileRepository.findByFolderFolderIdAndDeletedAtIsNotNull(folder.getFolderId())
                .forEach(file -> {
                    file.setDeletedAt(null);
                    file.setDeletedBy(null);
                });
    }

    // Favorite
    @Transactional
    public void toggleFavorite(UUID folderId, JwtAuthenticationToken token) {
        var user = getUser(token);

        Folder folder = folderRepository
                .findByFolderIdAndTownshipTownshipIdAndDeletedAtIsNull(
                        folderId,
                        user.getTownship().getTownshipId()
                )
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        folder.setFavorite(!folder.getFavorite());
    }

    // Auxiliary methods
    private User getUser(JwtAuthenticationToken token) {
        return userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private UUID getTownshipId(JwtAuthenticationToken token) {
        return getUser(token).getTownship().getTownshipId();
    }
}
