package com.stage.gestion_marches.controller;
import com.stage.gestion_marches.dto.UserDTO;
import com.stage.gestion_marches.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProfileController {

    private final UserService userService;

    // Endpoint pour mettre à jour son propre profil
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MARCHES', 'RESPONSABLE_GREEN_WOOD', 'ADMIN_SYSTEME')")
    public ResponseEntity<UserDTO> updateProfile(@PathVariable Long id, @RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }
}