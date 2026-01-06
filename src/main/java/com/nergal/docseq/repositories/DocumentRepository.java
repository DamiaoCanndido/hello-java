package com.nergal.docseq.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.nergal.docseq.entities.Document;

@NoRepositoryBean
public interface DocumentRepository<T extends Document>
        extends JpaRepository<T, UUID> {

    // Find documents by township ID ordered by 'order' field in descending order
    List<T> findByTownship_TownshipIdAndCreatedAtBetweenOrderByOrderDesc(
        UUID townshipId, 
        LocalDateTime startTimestamp, 
        LocalDateTime endTimestamp
    );

    // Find a document by its 'order' field
    T findByOrder(Integer order);

    // Count documents by township ID and created between two timestamps
    long countByTownship_TownshipIdAndCreatedAtBetween(UUID townshipId, LocalDateTime startTimestamp, LocalDateTime endTimestamp);
}

