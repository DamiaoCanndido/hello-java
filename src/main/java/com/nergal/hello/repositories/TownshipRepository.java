package com.nergal.hello.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nergal.hello.entities.Township;

@Repository
public interface TownshipRepository extends JpaRepository<Township, UUID>{

    Optional<Township> findByName(String name);

    Optional<Township> findByTownshipId(String id);

}

