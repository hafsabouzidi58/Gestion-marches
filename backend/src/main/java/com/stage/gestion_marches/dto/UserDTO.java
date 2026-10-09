package com.stage.gestion_marches.dto;

import com.stage.gestion_marches.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String motDePasseHash; // 👈 AJOUTÉ ICI
    private String email;
    private Role role;
    private boolean actif;

    public UserDTO(Long id, String nom, String prenom, Role role, boolean actif, String email) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.actif = actif;
        this.email = email;

    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }
    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public String getPrenom() {
        return prenom;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActif() {
        return actif;
    }
}