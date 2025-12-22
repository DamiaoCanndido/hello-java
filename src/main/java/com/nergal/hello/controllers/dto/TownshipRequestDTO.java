package com.nergal.hello.controllers.dto;

public record TownshipRequestDTO(
    String name, 
    String imageUrl, 
    String uf
) {}
