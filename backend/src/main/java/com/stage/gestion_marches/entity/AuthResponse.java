package com.stage.gestion_marches.entity;
import com.stage.gestion_marches.entity.Role;

public record AuthResponse(
        String token,
        String type,
        Long id,
        String email,
        String nom,
        String prenom,
        Role role
) {
    public AuthResponse(String token, Long id, String email, String nom, String prenom, Role role) {
        this(token, "Bearer", id, email, nom, prenom, role);
    }
}