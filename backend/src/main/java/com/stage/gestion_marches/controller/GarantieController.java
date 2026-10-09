package com.stage.gestion_marches.controller;

import com.stage.gestion_marches.dto.GarantieDTO;
import com.stage.gestion_marches.service.GarantieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/garanties")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GarantieController {

    private final GarantieService garantieService;

    @GetMapping
    public ResponseEntity<List<GarantieDTO>> getAllGaranties() {
        return ResponseEntity.ok(garantieService.getAllGaranties());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GarantieDTO> getGarantieById(@PathVariable Long id) {
        return ResponseEntity.ok(garantieService.getGarantieById(id));
    }

    @PostMapping
    public ResponseEntity<GarantieDTO> createGarantie(@RequestBody GarantieDTO dto) {
        return ResponseEntity.ok(garantieService.saveGarantie(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGarantie(@PathVariable Long id, @RequestBody GarantieDTO dto) {
        try {
            return ResponseEntity.ok(garantieService.updateGarantie(id, dto));
        } catch (Exception e) {
            e.printStackTrace(); // Affiche la ligne exacte de l'erreur dans la console
            return ResponseEntity.status(500).body("Erreur interne: " + e.getMessage());
        }
    }
    @PutMapping("/{id}/liberer")
    public ResponseEntity<GarantieDTO> libererGarantie(@PathVariable Long id) {
        return ResponseEntity.ok(garantieService.libererGarantie(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGarantie(@PathVariable Long id) {
        garantieService.deleteGarantie(id);
        return ResponseEntity.noContent().build();
    }
}