package com.nergal.hello.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_township")
public class Township {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "township_id")
    private UUID townshipId;

    @Column
    private String name;

    @Column(unique = true, length = 2)
    private String uf;

    @Column(name = "image_url")
    private String imageUrl;
}
