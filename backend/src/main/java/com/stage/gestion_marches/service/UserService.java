package com.stage.gestion_marches.service;

import com.stage.gestion_marches.dto.UserDTO;
import com.stage.gestion_marches.entity.Utilisateur;
import com.stage.gestion_marches.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 👈 Import de Transactional ajouté

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UtilisateurRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UtilisateurRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Lister tous les utilisateurs
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Chercher par email
    public List<UserDTO> searchByEmail(String email) {
        return userRepository.findByEmailContainingIgnoreCase(email).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Modifier un utilisateur
    @Transactional
    public UserDTO updateUser(Long id, UserDTO dto) {
        // 👈 Correction : userRepository au lieu de utilisateurRepository
        Utilisateur user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id : " + id));

        user.setNom(dto.getNom());
        user.setPrenom(dto.getPrenom());
        user.setEmail(dto.getEmail());

        // Hacher et mettre à jour le mot de passe seulement s'il a été renseigné
        if (dto.getMotDePasseHash() != null && !dto.getMotDePasseHash().trim().isEmpty()) {
            user.setMotDePasseHash(passwordEncoder.encode(dto.getMotDePasseHash()));
        }

        Utilisateur updated = userRepository.save(user);
        // 👈 Correction : mapToDTO au lieu de convertToDTO
        return mapToDTO(updated);
    }

    // Activer / Désactiver un compte
    public UserDTO toggleUserStatus(Long id) {
        Utilisateur user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setActif(!user.isActif()); // Inverse le statut
        Utilisateur updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    // Helper de conversion Entity -> DTO
    private UserDTO mapToDTO(Utilisateur user) {
        return new UserDTO(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getRole(),
                user.isActif(),
                user.getEmail()
        );
    }
}