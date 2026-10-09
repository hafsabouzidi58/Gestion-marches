package com.stage.gestion_marches.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stage.gestion_marches.dto.AvancementCreateDTO;
import com.stage.gestion_marches.dto.AvancementResponseDTO;
import com.stage.gestion_marches.dto.PenalitePredictionDTO;
import com.stage.gestion_marches.service.AvancementService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/avancements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AvancementController {

    private final AvancementService avancementService;

    // Déclarer un nouvel avancement avec pièces jointes
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> enregistrerAvancement(
            @RequestParam("avancement") String avancementJson,
            @RequestPart(value = "fichiers", required = false) List<MultipartFile> fichiers) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            AvancementCreateDTO dto = mapper.readValue(avancementJson, AvancementCreateDTO.class);

            AvancementResponseDTO response = avancementService.enregistrerAvancement(dto, fichiers);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Modifier un avancement existant (avec ajouts et/ou suppressions de pièces jointes)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> modifierAvancement(
            @PathVariable("id") Long id,
            @RequestParam("avancement") String avancementJson,
            @RequestPart(value = "fichiers", required = false) List<MultipartFile> nouveauxFichiers,
            @RequestParam(value = "supprimerFichierIds", required = false) List<Long> idsFichiersASupprimer) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            AvancementCreateDTO dto = mapper.readValue(avancementJson, AvancementCreateDTO.class);

            AvancementResponseDTO response = avancementService.modifierAvancement(id, dto, nouveauxFichiers, idsFichiersASupprimer);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Consulter l'historique d'un marché
    @GetMapping("/marche/{marcheId}")
    public ResponseEntity<List<AvancementResponseDTO>> getHistorique(@PathVariable Long marcheId) {
        return ResponseEntity.ok(avancementService.getHistoriqueByMarche(marcheId));
    }

    // Obtenir la prédiction des pénalités
    @GetMapping("/prediction/marche/{marcheId}")
    public ResponseEntity<PenalitePredictionDTO> getPredictionPenalites(@PathVariable Long marcheId) {
        return ResponseEntity.ok(avancementService.simulerPredictionPenalites(marcheId));
    }

    // Rechercher dans l'historique des avancements par mot-clé
    @GetMapping("/marche/{marcheId}/search")
    public ResponseEntity<List<AvancementResponseDTO>> rechercherAvancements(
            @PathVariable("marcheId") Long marcheId,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return ResponseEntity.ok(avancementService.rechercherParMotCle(marcheId, keyword));
    }

    // Consulter / Télécharger un fichier média ou PDF
    @GetMapping("/fichiers/{nomFichier:.+}")
    public ResponseEntity<Resource> telechargerFichier(@PathVariable String nomFichier) {
        try {
            Path filePath = Paths.get("uploads/avancements/").resolve(nomFichier).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Détection automatique du type de fichier (image/jpeg, application/pdf, etc.)
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Supprimer un avancement
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerAvancement(@PathVariable Long id) {
        try {
            avancementService.supprimerAvancement(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping("/alertes")
    public ResponseEntity<List<PenalitePredictionDTO>> getToutesLesAlertes() {
        return ResponseEntity.ok(avancementService.getToutesLesAlertesPenalites());
    }
}