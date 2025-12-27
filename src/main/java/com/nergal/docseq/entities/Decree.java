package com.nergal.docseq.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_decree")
public class Decree extends Document { }