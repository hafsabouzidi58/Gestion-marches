package com.stage.gestion_marches.service;
import com.stage.gestion_marches.dto.*;
import com.stage.gestion_marches.entity.*;
import com.stage.gestion_marches.repository.*;
import com.stage.gestion_marches.service.ProblemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemeServiceImpl implements ProblemeService {

    private final ProblemeMarcheRepository problemeRepo;
    private final MessageProblemeRepository messageRepo;
    private final PieceJointeProblemeRepository pieceRepo;
    private final MarcheRepository marcheRepo;
    private final UtilisateurRepository utilisateurRepo;
    private final PieceJointeProblemeRepository pieceJointeRepo;
    private final DocumentService documentService;
    private final String UPLOAD_DIR = "uploads/problemes/";

    @Override
    @Transactional
    public ProblemeResponseDTO declarerProbleme(ProblemeCreateDTO dto, Long utilisateurId, List<MultipartFile> fichiers) {
        Marche marche = marcheRepo.findById(dto.getMarcheId())
                .orElseThrow(() -> new RuntimeException("Marché introuvable"));

        Utilisateur auteur = utilisateurRepo.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // 1. Créer le problème
        ProblemeMarche probleme = ProblemeMarche.builder()
                .titre(dto.getTitre())
                .marche(marche)
                .description(dto.getDescription())
                .priorite(dto.getPriorite())
                .declarePar(auteur)
                .etat(EtatProbleme.EN_ATTENTE)
                .dateDeclaration(LocalDateTime.now())
                .build();

        ProblemeMarche savedProbleme = problemeRepo.save(probleme);

        // 2. Créer le premier message automatique avec la description
        MessageProbleme premierMessage = MessageProbleme.builder()
                .probleme(savedProbleme)
                .auteur(auteur)
                .contenu(dto.getDescription())
                .dateEnvoi(LocalDateTime.now())
                .build();

        MessageProbleme savedMessage = messageRepo.save(premierMessage);

        // 3. Enregistrer les pièces jointes si présentes
        if (fichiers != null && !fichiers.isEmpty()) {
            enregistrerPiecesJointes(fichiers, savedMessage);
        }

        return mapToDTO(savedProbleme);
    }

    @Override
    @Transactional
    public MessageResponseDTO ajouterMessage(Long problemeId, MessageCreateDTO dto, Long utilisateurId, List<MultipartFile> fichiers) {
        ProblemeMarche probleme = problemeRepo.findById(problemeId)
                .orElseThrow(() -> new RuntimeException("Problème introuvable : " + problemeId));

        Utilisateur auteur = utilisateurRepo.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + utilisateurId));

        // 1. Enregistrer le message
        MessageProbleme message = MessageProbleme.builder()
                .probleme(probleme)
                .auteur(auteur)
                .contenu(dto.getContenu())
                .dateEnvoi(LocalDateTime.now())
                .build();

        MessageProbleme messageSauvegarde = messageRepo.save(message);
        List<PieceJointeProbleme> piecesJointes = new ArrayList<>();

        // 2. Traiter les pièces jointes
        if (fichiers != null && !fichiers.isEmpty()) {
            for (MultipartFile file : fichiers) {
                if (!file.isEmpty()) {
                    try {
                        // Stockage physique via DocumentService
                        String cheminFichier = documentService.enregistrerFichierMessage(file);

                        // Enregistrement en BDD dans piece_jointe_probleme
                        PieceJointeProbleme pj = PieceJointeProbleme.builder()
                                .nomFichier(file.getOriginalFilename())
                                .typeFichier(file.getContentType())
                                .cheminFichier(cheminFichier)
                                .dateDepot(LocalDateTime.now())
                                .message(messageSauvegarde)
                                .build();

                        PieceJointeProbleme pjSauvegardee = pieceJointeRepo.save(pj);
                        piecesJointes.add(pjSauvegardee);

                    } catch (IOException e) {
                        throw new RuntimeException("Erreur lors du stockage du fichier : " + file.getOriginalFilename(), e);
                    }
                }
            }
        }

        // 3. Rattacher la liste des pièces jointes pour le DTO de retour
        messageSauvegarde.setPiecesJointes(piecesJointes);

        return mapToMessageResponseDTO(messageSauvegarde);
    }

    @Override
    @Transactional
    public ProblemeResponseDTO changerEtat(Long problemeId, EtatProbleme nouvelEtat) {
        ProblemeMarche probleme = problemeRepo.findById(problemeId)
                .orElseThrow(() -> new RuntimeException("Problème introuvable"));

        probleme.setEtat(nouvelEtat);
        if (nouvelEtat == EtatProbleme.RESOLU) {
            probleme.setDateResolution(LocalDateTime.now());
        }

        return mapToDTO(problemeRepo.save(probleme));
    }

    @Override
    public ProblemeResponseDTO getProblemeById(Long id) {
        return problemeRepo.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Problème introuvable"));
    }

    @Override
    public List<ProblemeResponseDTO> getProblemesByMarche(Long marcheId) {
        return problemeRepo.findByMarcheIdOrderByDateDeclarationDesc(marcheId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProblemeResponseDTO> getAllProblemes() {
        return problemeRepo.findAll()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // --- Helper Methods ---
    private void enregistrerPiecesJointes(List<MultipartFile> fichiers, MessageProbleme message) {
        try {
            Path rootPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(rootPath)) {
                Files.createDirectories(rootPath);
            }

            for (MultipartFile file : fichiers) {
                if (file.isEmpty()) continue;

                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = rootPath.resolve(filename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                PieceJointeProbleme pj = PieceJointeProbleme.builder()
                        .message(message)
                        .nomFichier(file.getOriginalFilename())
                        .cheminFichier(filePath.toString())
                        .typeFichier(file.getContentType())
                        .dateDepot(LocalDateTime.now())
                        .build();

                pieceRepo.save(pj);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement des fichiers", e);
        }
    }
    private ProblemeResponseDTO mapToDTO(ProblemeMarche entity) {
        ProblemeResponseDTO dto = new ProblemeResponseDTO();
        dto.setId(entity.getId());
        dto.setTitre(entity.getTitre());
        dto.setMarcheId(entity.getMarche().getId());

        // Sécurité au cas où numMarche est nul
        if (entity.getMarche() != null) {
            dto.setNumMarche(entity.getMarche().getNumMarche());
        }

        dto.setDescription(entity.getDescription());
        dto.setEtat(entity.getEtat());
        dto.setPriorite(entity.getPriorite());
        dto.setDateDeclaration(entity.getDateDeclaration());
        dto.setDateResolution(entity.getDateResolution());

        if (entity.getDeclarePar() != null) {
            dto.setDeclareParId(entity.getDeclarePar().getId());
            dto.setDeclareParNom(entity.getDeclarePar().getNom());
        }

        // Listes initiales pour éviter les NullPointerExceptions
        List<MessageResponseDTO> messageDTOs = new ArrayList<>();
        List<PieceJointeResponseDTO> piecesDeclaration = new ArrayList<>();

        if (entity.getMessages() != null && !entity.getMessages().isEmpty()) {
            messageDTOs = entity.getMessages().stream()
                    .map(this::mapToMessageDTO)
                    .collect(Collectors.toList());

            // Récupérer les pièces jointes du 1er message (la déclaration initiale)
            MessageResponseDTO premierMessage = messageDTOs.get(0);
            if (premierMessage.getPiecesJointes() != null) {
                piecesDeclaration = premierMessage.getPiecesJointes();
            }
        }

        dto.setMessages(messageDTOs);
        dto.setPiecesJointesDeclaration(piecesDeclaration); // 👈 Maintenant les types correspondent parfaitement

        return dto;
    }
    private MessageResponseDTO mapToMessageDTO(MessageProbleme message) {
        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setId(message.getId());
        dto.setAuteurId(message.getAuteur().getId());
        dto.setAuteurNom(message.getAuteur().getNom());
        dto.setContenu(message.getContenu());
        dto.setDateEnvoi(message.getDateEnvoi());

        if (message.getPiecesJointes() != null) {
            dto.setPiecesJointes(message.getPiecesJointes().stream().map(pj -> {
                PieceJointeResponseDTO pjDto = new PieceJointeResponseDTO();
                pjDto.setId(pj.getId());
                pjDto.setNomFichier(pj.getNomFichier());
                pjDto.setCheminFichier(pj.getCheminFichier());
                pjDto.setTypeFichier(pj.getTypeFichier());
                pjDto.setDateDepot(pj.getDateDepot());
                return pjDto;
            }).collect(Collectors.toList()));
        }

        return dto;
    }

    @Override
    public List<ProblemeResponseDTO> getProblemesByUtilisateur(Long utilisateurId) {
        return problemeRepo.findByDeclareParIdOrderByDateDeclarationDesc(utilisateurId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private MessageResponseDTO mapToMessageResponseDTO(MessageProbleme msg) {
        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setId(msg.getId());
        dto.setContenu(msg.getContenu());
        dto.setDateEnvoi(msg.getDateEnvoi());
        dto.setAuteurId(msg.getAuteur().getId());
        dto.setAuteurNom(msg.getAuteur().getNom() + " " + msg.getAuteur().getPrenom());

        if (msg.getPiecesJointes() != null && !msg.getPiecesJointes().isEmpty()) {
            List<PieceJointeResponseDTO> pjDtos = msg.getPiecesJointes().stream().map(pj -> {
                PieceJointeResponseDTO pDto = new PieceJointeResponseDTO();
                pDto.setId(pj.getId());
                pDto.setNomFichier(pj.getNomFichier());
                pDto.setTypeFichier(pj.getTypeFichier());
                return pDto;
            }).collect(Collectors.toList());

            dto.setPiecesJointes(pjDtos);
        } else {
            dto.setPiecesJointes(new ArrayList<>());
        }

        return dto;
    }
}