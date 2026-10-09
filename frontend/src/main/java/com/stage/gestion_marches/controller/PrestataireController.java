package com.stage.gestion_marches.controller;

import com.stage.gestion_marches.dto.PrestataireDTO;
import com.stage.gestion_marches.service.PrestataireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestataires")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PrestataireController {

    private final PrestataireService prestataireService;

    // ✅ Autorise RESPONSABLE_GREEN_WOOD à lister
    @GetMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE_MARCHES', 'RESPONSABLE_GREEN_WOOD', 'ADMIN_SYSTEME')")
    public ResponseEntity<List<PrestataireDTO>> getAll() {
        return ResponseEntity.ok(prestataireService.getAllPrestataires());
    }

    @GetMapping("/actifs")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MARCHES', 'RESPONSABLE_GREEN_WOOD', 'ADMIN_SYSTEME')")
    public ResponseEntity<List<PrestataireDTO>> getActifs() {
        return ResponseEntity.ok(prestataireService.getActivePrestataires());
    }

    // ✅ Autorise RESPONSABLE_GREEN_WOOD à récupérer son profil par ID (ex: ID 4)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MARCHES', 'RESPONSABLE_GREEN_WOOD', 'ADMIN_SYSTEME')")
    public ResponseEntity<PrestataireDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(prestataireService.getPrestataireById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('RESPONSABLE_MARCHES')")
    public ResponseEntity<PrestataireDTO> create(@RequestBody PrestataireDTO dto) {
        return new ResponseEntity<>(prestataireService.createPrestataire(dto), HttpStatus.CREATED);
    }

    // ✅ Autorise RESPONSABLE_GREEN_WOOD à modifier ses données
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MARCHES', 'RESPONSABLE_GREEN_WOOD')")
    public ResponseEntity<PrestataireDTO> update(@PathVariable Long id, @RequestBody PrestataireDTO dto) {
        return ResponseEntity.ok(prestataireService.updatePrestataire(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RESPONSABLE_MARCHES')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prestataireService.deletePrestataire(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('RESPONSABLE_MARCHES')")
    public ResponseEntity<Void> toggleActif(@PathVariable Long id) {
        prestataireService.toggleActif(id);
        return ResponseEntity.ok().build();
    }
}