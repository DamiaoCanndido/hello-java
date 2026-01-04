package com.nergal.docseq.controllers.dto;

import java.util.UUID;

import com.nergal.docseq.controllers.validators.PasswordMatches;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@PasswordMatches
public record UpdateUserDTO(
    @Min(value = 3, message = "Username must be at least 3 characters long")
    String username, 

    @Email(message = "Email should be valid")
    String email,

    String role,

    @Size(min = 6, message = "Password must have at least 6 characters")
    String password, 

    @Size(min = 6, message = "Password must have at least 6 characters")
    String confirmPassword,

    UUID townshipId
) implements PasswordConfirmable { }
