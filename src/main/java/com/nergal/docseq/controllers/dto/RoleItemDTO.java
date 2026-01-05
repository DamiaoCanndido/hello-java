package com.nergal.docseq.controllers.dto;

import com.nergal.docseq.entities.Role;

public record RoleItemDTO(Long roleId, Role.Values name) {

}
