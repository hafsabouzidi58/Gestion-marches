package com.stage.gestion_marches.controller;
import com.stage.gestion_marches.entity.PieceJointeProbleme;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stage.gestion_marches.dto.*;
import com.stage.gestion_marches.entity.EtatProbleme;
import com.stage.gestion_marches.repository.PieceJointeProblemeRepository;
import com.stage.gestion_marches.service.ProblemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/problemes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProblemeController {

    private final ProblemeService problemeService;
private final PieceJointeProblemeRepository pieceRepo;
    // Déclarer un nouveau problème avec pièces jointes optionnelles

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> declarerProbleme(
            @RequestParam("probleme") String problemeJson,
            @RequestParam("utilisateurId") Long utilisateurId,
            @RequestPart(value = "fichiers", required = false) List<MultipartFile> fichiers) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ProblemeCreateDTO dto = mapper.readValue(problemeJson, ProblemeCreateDTO.class);

            ProblemeResponseDTO response = problemeService.declarerProbleme(dto, utilisateurId, fichiers);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // Affiche la cause exacte dans la console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping(value = "/{id}/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponseDTO> ajouterMessage(
            @PathVariable("id") Long problemeId,
            @RequestParam("message") String messageJson,
            @RequestParam("utilisateurId") Long utilisateurId,
            @RequestPart(value = "fichiers", required = false) List<MultipartFile> fichiers) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        MessageCreateDTO dto = mapper.readValue(messageJson, MessageCreateDTO.class);

        MessageResponseDTO response = problemeService.ajouterMessage(problemeId, dto, utilisateurId, fichiers);
        return ResponseEntity.ok(response);
    }

    // Changer l'état d'un problème (Responsable de marché)
    @PatchMapping("/{id}/etat")
    public ResponseEntity<ProblemeResponseDTO> changerEtat(
            @PathVariable("id") Long problemeId,
            @RequestParam("etat") EtatProbleme nouvelEtat) {

        return ResponseEntity.ok(problemeService.changerEtat(problemeId, nouvelEtat));
    }

    // Obtenir les détails d'un problème
    @GetMapping("/{id}")
    public ResponseEntity<ProblemeResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(problemeService.getProblemeById(id));
    }

    // Obtenir tous les problèmes liés à un marché spécifique
    @GetMapping("/marche/{marcheId}")
    public ResponseEntity<List<ProblemeResponseDTO>> getByMarche(@PathVariable("marcheId") Long marcheId) {
        return ResponseEntity.ok(problemeService.getProblemesByMarche(marcheId));
    }

    // Obtenir la liste globale de tous les problèmes
    @GetMapping
    public ResponseEntity<List<ProblemeResponseDTO>> getAll() {
        return ResponseEntity.ok(problemeService.getAllProblemes());
    }

    @GetMapping("/fichiers/{id}")
    public ResponseEntity<Resource> telechargerFichier(@PathVariable Long id) {
        try {
            // 1. Chercher les métadonnées de la pièce jointe en BDD
            PieceJointeProbleme pj = pieceRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pièce jointe introuvable : " + id));

            // 2. Charger le fichier à partir de son chemin enregistré
            Path filePath = Paths.get(pj.getCheminFichier()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = pj.getTypeFichier() != null ? pj.getTypeFichier() : "application/octet-stream";

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pj.getNomFichier() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<ProblemeResponseDTO>> getByUtilisateur(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(problemeService.getProblemesByUtilisateur(utilisateurId));
    }
}