package com.stage.gestion_marches.controller;
import com.stage.gestion_marches.dto.MarcheDTO;
import com.stage.gestion_marches.service.MarcheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marches")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MarcheController {

    private final MarcheService marcheService;

    @GetMapping
    public ResponseEntity<List<MarcheDTO>> getAll() {
        return ResponseEntity.ok(marcheService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarcheDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(marcheService.getById(id));
    }
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Void> toggleStatus(@PathVariable Long id) {
        marcheService.toggleActif(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping
    public ResponseEntity<MarcheDTO> create(@RequestBody MarcheDTO dto) {
        return ResponseEntity.ok(marcheService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarcheDTO> update(@PathVariable Long id, @RequestBody MarcheDTO dto) {
        return ResponseEntity.ok(marcheService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        marcheService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<MarcheDTO>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String exercice) {
        return ResponseEntity.ok(marcheService.search(keyword, exercice));
    }

    // 💡 REST ENDPOINT INTELLIGENT : Détecte automatiquement les marchés à renouveler d'ici N jours (30 par défaut)
    @GetMapping("/alertes-echeance")
    public ResponseEntity<List<MarcheDTO>> getAlertesEcheance(
            @RequestParam(defaultValue = "30") int jours) {
        return ResponseEntity.ok(marcheService.getAlertesEcheance(jours));
    }

}