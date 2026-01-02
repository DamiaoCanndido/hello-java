package com.nergal.docseq.controllers.dto;

import java.util.UUID;

import com.nergal.docseq.controllers.validators.PasswordMatches;

@PasswordMatches
public record UpdateUserDTO(
    String username, 
    String email, 
    String role,
    String password, 
    String confirmPassword,
    UUID townshipId
) implements PasswordConfirmable { }
