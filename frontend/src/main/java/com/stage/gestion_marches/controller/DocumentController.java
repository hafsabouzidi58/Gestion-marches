package com.stage.gestion_marches.controller;

import com.stage.gestion_marches.dto.DocumentDTO;
import com.stage.gestion_marches.service.DocumentService;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", exposedHeaders = {HttpHeaders.CONTENT_DISPOSITION})
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Ajouter un document à un marché.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("marcheId") Long marcheId,
            @RequestParam(value = "typeDocument", required = false, defaultValue = "DOCUMENT") String typeDocument,
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("Le fichier est vide.");
            }

            if (marcheId == null) {
                return ResponseEntity.badRequest().body("L'identifiant du marché est obligatoire.");
            }

            Long uploadeParId = 1L; // ID par défaut ou extrait du token

            DocumentDTO savedDoc = documentService.uploadDocument(
                    marcheId,
                    typeDocument,
                    uploadeParId,
                    file
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(savedDoc);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'ajout du document : " + e.getMessage());
        }
    }

    @GetMapping("/marche/{marcheId}")
    public ResponseEntity<List<DocumentDTO>> getByMarche(@PathVariable Long marcheId) {
        List<DocumentDTO> documents = documentService.getByMarche(marcheId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadDocument(@PathVariable Long id) {
        try {
            DocumentDTO document = documentService.getById(id);
            Resource resource = documentService.loadFileAsResource(id);
            String contentType = documentService.getContentType(id);

            ContentDisposition contentDisposition = ContentDisposition.attachment()
                    .filename(document.getNomFichier(), StandardCharsets.UTF_8)
                    .build();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                    .body(resource);

        } catch ( RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du téléchargement du fichier.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        try {
            documentService.delete(id);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression du fichier sur le disque.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur lors de la suppression du document.");
        }
    }
}