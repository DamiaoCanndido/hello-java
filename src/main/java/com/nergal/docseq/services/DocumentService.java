package com.nergal.docseq.services;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

import com.nergal.docseq.controllers.dto.DocumentDTO;
import com.nergal.docseq.controllers.dto.DocumentItemDTO;
import com.nergal.docseq.controllers.dto.DocumentRequestDTO;
import com.nergal.docseq.controllers.dto.UpdateDocumentDTO;
import com.nergal.docseq.controllers.helpers.DateRange;
import com.nergal.docseq.entities.Document;
import com.nergal.docseq.exception.ConflictException;
import com.nergal.docseq.exception.NotFoundException;
import com.nergal.docseq.repositories.DocumentRepository;
import com.nergal.docseq.repositories.UserRepository;

@Transactional
public abstract class DocumentService<T extends Document> {

    protected final DocumentRepository<T> repository;
    protected final UserRepository userRepository;

    protected DocumentService(
            DocumentRepository<T> repository,
            UserRepository userRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    protected DocumentDTO listDocumentsByTownship(
        int page,
        int pageSize,
        JwtAuthenticationToken token) {

        var pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "order");
        var township_id = userRepository.findById(UUID.fromString(token.getName()))
            .get().getTownship().getTownshipId();
            var dateRange = new DateRange();
            var initialDateTime = dateRange.getInitialDateTime();
            var endDateTime = dateRange.getEndDateTime();
            var documents = repository.findByTownship_TownshipIdAndCreatedAtBetweenOrderByOrderDesc(
                township_id, 
                initialDateTime, 
                endDateTime, 
                pageable
            );
            List<DocumentItemDTO> documentsItems = documents
            .stream()
            .map(doc -> 
                new DocumentItemDTO(
                    doc.getId(),
                    doc.getDescription(),
                    doc.getOrder(),
                    doc.getCreatedAt()
                )
            ).toList();

        return new DocumentDTO(
            documentsItems, 
            page, 
            pageSize, 
            documents.getTotalPages(), 
            documents.getTotalElements()
        );
    }

    protected T createBase(
            DocumentRequestDTO dto,
            JwtAuthenticationToken token,
            Supplier<T> factory
    ) {
        var dateRange = new DateRange();
        var user = userRepository.findById(UUID.fromString(token.getName()));
        var documentAlreadyExists = repository.findByOrderAndCreatedAtBetween(
            dto.order(), 
            dateRange.getInitialDateTime(), 
            dateRange.getEndDateTime()
        );

        if (documentAlreadyExists != null) {
            throw new ConflictException(
                "Document with order " + dto.order() + " already exists."
            );
        }

        int lastNoticeOrderByTownship = 0;
        
            var initialDateTime = dateRange.getInitialDateTime();
            var endDateTime = dateRange.getEndDateTime();
        var pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "order");

        var noticeList = repository.findByTownship_TownshipIdAndCreatedAtBetweenOrderByOrderDesc(
            user.get().getTownship().getTownshipId(), initialDateTime, endDateTime, pageable).getContent();

        if (!noticeList.isEmpty()) {
            lastNoticeOrderByTownship = noticeList.get(0).getOrder();
        }

        if (dto.order() == null) {
            dto = new DocumentRequestDTO(
                lastNoticeOrderByTownship + 1,
                dto.description(),
                dto.townshipId()
            );
        }

        var document = factory.get();
        document.setCreatedBy(user.get());
        document.setTownship(user.get().getTownship());
        document.setDescription(dto.description());
        document.setOrder(dto.order());

        return repository.save(document);
    }

    protected void applyUpdates(T entity, UpdateDocumentDTO dto) {

        if (dto.order() != null) {
            var dateRange = new DateRange();
            var exists = repository.findByOrderAndCreatedAtBetween(
                dto.order(), 
                dateRange.getInitialDateTime(), 
                dateRange.getEndDateTime()
            );

            if (exists != null && !exists.getOrder().equals(entity.getOrder())) {
                throw new ConflictException(
                    "Document with order " + dto.order() + " already exists."
                );
            }

            entity.setOrder(dto.order());
        }

        if (dto.description() != null) {
            entity.setDescription(dto.description());
        }
    }

    public T update(UUID id, UpdateDocumentDTO dto) {
        T entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException(
                    "Document not found"
            ));

        applyUpdates(entity, dto);

        return repository.save(entity);
    }

    public void deleteDocument(UUID documentId) {
        var document = repository.findById(documentId)
            .orElseThrow(() -> new NotFoundException(
                    "Document not found"
            ));

        repository.delete(document);
    }
}

