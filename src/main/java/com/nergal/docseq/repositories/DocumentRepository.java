package com.nergal.docseq.repositories;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.nergal.docseq.entities.Document;

@NoRepositoryBean
public interface DocumentRepository<T extends Document>
        extends JpaRepository<T, UUID> {

    // Find documents by township ID ordered by 'order' field in descending order
    Page<T> findByTownship_TownshipIdAndCreatedAtBetweenOrderByOrderDesc(
        UUID townshipId, 
        LocalDateTime startTimestamp, 
        LocalDateTime endTimestamp,
        Pageable pageable
    );

    // Find a document by its 'order' field
    T findByOrderAndCreatedAtBetween(Integer order, LocalDateTime startTimestamp, LocalDateTime endTimestamp);

    // Count documents by township ID and created between two timestamps
    long countByTownship_TownshipIdAndCreatedAtBetween(UUID townshipId, LocalDateTime startTimestamp, LocalDateTime endTimestamp);
}

