package com.stage.gestion_marches.controller;

import com.stage.gestion_marches.dto.DecompteDTO;
import com.stage.gestion_marches.service.DecompteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/decomptes")
public class DecompteController {

    private final DecompteService decompteService;

    public DecompteController(DecompteService decompteService) {
        this.decompteService = decompteService;
    }

    @PostMapping
    public ResponseEntity<DecompteDTO> ajouterDecompte(@Valid @RequestBody DecompteDTO dto) {
        return ResponseEntity.ok(decompteService.enregistrerDecompte(dto));
    }

    @GetMapping("/marche/{marcheId}")
    public ResponseEntity<List<DecompteDTO>> getByMarche(@PathVariable Long marcheId) {
        return ResponseEntity.ok(decompteService.getDecomptesByMarche(marcheId));
    }

    @GetMapping("/solde/{marcheId}")
    public ResponseEntity<BigDecimal> getSoldeRestant(@PathVariable Long marcheId) {
        return ResponseEntity.ok(decompteService.calculerSoldeRestantMarche(marcheId));
    }
}