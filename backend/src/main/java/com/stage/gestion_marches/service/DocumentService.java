package com.stage.gestion_marches.service;

import com.stage.gestion_marches.dto.DocumentDTO;
import com.stage.gestion_marches.entity.Document;
import com.stage.gestion_marches.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    /**
     * Téléversement d'un document.
     */
    @Transactional
    public DocumentDTO uploadDocument(
            Long marcheId,
            String typeDocument,
            Long uploadePar,
            MultipartFile file) throws IOException {

        if (marcheId == null) {
            throw new IllegalArgumentException("L'identifiant du marché est obligatoire.");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide.");
        }
        if (typeDocument == null || typeDocument.trim().isEmpty()) {
            throw new IllegalArgumentException("Le type du document est obligatoire.");
        }

        String uploadDir = System.getProperty("user.dir")
                + File.separator + "uploads"
                + File.separator + "marches";

        Path storagePath = Paths.get(uploadDir).toAbsolutePath().normalize();

        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            originalFileName = "document";
        }
        originalFileName = Paths.get(originalFileName).getFileName().toString();

        String storedFileName = UUID.randomUUID() + "_" + originalFileName;
        Path targetLocation = storagePath.resolve(storedFileName).normalize();

        if (!targetLocation.startsWith(storagePath)) {
            throw new IllegalArgumentException("Nom de fichier invalide.");
        }

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        Document document = Document.builder()
                .marcheId(marcheId)
                .uploadePar(uploadePar != null ? uploadePar : 1L)
                .nomFichier(originalFileName)
                .typeDocument(typeDocument.trim())
                .cheminFichier(targetLocation.toString())
                .tailleKo(calculateSizeKo(file.getSize()))
                .createdAt(LocalDateTime.now())
                .build();

        Document savedDocument = documentRepository.save(document);
        return convertToDTO(savedDocument);
    }

    /**
     * Récupérer les documents d'un marché.
     */
    @Transactional(readOnly = true)
    public List<DocumentDTO> getByMarche(Long marcheId) {
        if (marcheId == null) {
            throw new IllegalArgumentException("L'identifiant du marché est obligatoire.");
        }
        return documentRepository.findByMarcheId(marcheId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un document DTO par son ID.
     */
    @Transactional(readOnly = true)
    public DocumentDTO getById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document introuvable avec l'ID : " + id));
        return convertToDTO(document);
    }

    /**
     * Charger la ressource physique d'un fichier.
     */
    @Transactional(readOnly = true)
    public Resource loadFileAsResource(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document introuvable avec l'ID : " + id));

        try {
            Path filePath = Paths.get(document.getCheminFichier()).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("Le fichier physique est introuvable sur le serveur.");
            }
            if (!resource.isReadable()) {
                throw new RuntimeException("Droits insuffisants pour lire le fichier sur le serveur.");
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Chemin réseau ou fichier invalide.", e);
        }
    }

    /**
     * Déterminer le type MIME du fichier.
     */
    @Transactional(readOnly = true)
    public String getContentType(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document introuvable avec l'ID : " + id));

        try {
            Path filePath = Paths.get(document.getCheminFichier()).normalize();
            String contentType = Files.probeContentType(filePath);
            return (contentType != null) ? contentType : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }

    /**
     * Supprimer un fichier (disque + base de données).
     */
    @Transactional
    public void delete(Long id) throws IOException {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document introuvable avec l'ID : " + id));

        if (document.getCheminFichier() != null) {
            Path filePath = Paths.get(document.getCheminFichier()).toAbsolutePath().normalize();
            Files.deleteIfExists(filePath);
        }

        documentRepository.delete(document);
    }

    private DocumentDTO convertToDTO(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setMarcheId(document.getMarcheId());
        dto.setUploadePar(document.getUploadePar());
        dto.setNomFichier(document.getNomFichier());
        dto.setTypeDocument(document.getTypeDocument());
        dto.setCheminFichier(document.getCheminFichier());
        dto.setTailleKo(document.getTailleKo());
        dto.setCreatedAt(document.getCreatedAt());
        return dto;
    }

    private Long calculateSizeKo(long sizeBytes) {
        if (sizeBytes == 0) return 0L;
        return (long) Math.ceil(sizeBytes / 1024.0);
    }

    /**
     * Sauvegarder un fichier physique sur le disque (dans uploads/messages).
     * @return Le chemin absolu du fichier enregistré.
     */
    public String enregistrerFichierMessage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide.");
        }

        String uploadDir = System.getProperty("user.dir")
                + File.separator + "uploads"
                + File.separator + "messages";

        Path storagePath = Paths.get(uploadDir).toAbsolutePath().normalize();

        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            originalFileName = "fichier";
        }
        originalFileName = Paths.get(originalFileName).getFileName().toString();

        String storedFileName = UUID.randomUUID() + "_" + originalFileName;
        Path targetLocation = storagePath.resolve(storedFileName).normalize();

        if (!targetLocation.startsWith(storagePath)) {
            throw new IllegalArgumentException("Nom de fichier invalide.");
        }

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return targetLocation.toString();
    }
}