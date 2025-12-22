package com.nergal.hello.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nergal.hello.controllers.dto.TownshipRequestDTO;
import com.nergal.hello.entities.Township;
import com.nergal.hello.repositories.TownshipRepository;

@Service
public class TownshipService {

    private final TownshipRepository townshipRepo;
    
    public TownshipService(TownshipRepository townshipRepo) {
        this.townshipRepo = townshipRepo;
    }

    @Transactional
    public void createTownship(TownshipRequestDTO dto, UUID townshipId){
        var township = new Township();
        township.setName(dto.name());
        township.setUf(dto.uf());
        township.setImageUrl(dto.imageUrl());
        townshipRepo.save(township);
    }
}
