package com.stage.gestion_marches.controller;

import com.stage.gestion_marches.dto.NantissementDTO;
import com.stage.gestion_marches.service.NantissementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nantissements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NantissementController {

    private final NantissementService nantissementService;

    // GET /api/nantissements/search?keyword=Attijari
    @GetMapping("/search")
    public ResponseEntity<List<NantissementDTO>> search(@RequestParam(required = false, defaultValue = "") String keyword) {
        return ResponseEntity.ok(nantissementService.search(keyword));
    }

    // GET /api/marches/{marcheId}/nantissement
    @GetMapping("/marche/{marcheId}")
    public ResponseEntity<NantissementDTO> getByMarcheId(@PathVariable Long marcheId) {
        return ResponseEntity.ok(nantissementService.getByMarcheId(marcheId));
    }

    // POST /api/marches/{marcheId}/nantissement
    @PostMapping("/marche/{marcheId}")
    public ResponseEntity<NantissementDTO> saveOrUpdate(
            @PathVariable Long marcheId,
            @RequestBody NantissementDTO dto) {
        return ResponseEntity.ok(nantissementService.saveOrUpdate(marcheId, dto));
    }

    // DELETE /api/marches/{marcheId}/nantissement
    @DeleteMapping("/marche/{marcheId}")
    public ResponseEntity<Void> deleteByMarcheId(@PathVariable Long marcheId) {
        nantissementService.deleteByMarcheId(marcheId);
        return ResponseEntity.noContent().build();
    }
}