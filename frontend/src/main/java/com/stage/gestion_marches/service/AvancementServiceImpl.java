package com.stage.gestion_marches.service;

import com.stage.gestion_marches.dto.*;
import com.stage.gestion_marches.entity.Avancement;
import com.stage.gestion_marches.entity.Marche;
import com.stage.gestion_marches.entity.PieceJointeAvancement;
import com.stage.gestion_marches.repository.AvancementRepository;
import com.stage.gestion_marches.repository.MarcheRepository;
import com.stage.gestion_marches.service.AvancementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvancementServiceImpl implements AvancementService {

    private final AvancementRepository avancementRepo;
    private final MarcheRepository marcheRepo;

    @Override
    @Transactional
    public AvancementResponseDTO enregistrerAvancement(AvancementCreateDTO dto, List<MultipartFile> fichiers) {
        Marche marche = marcheRepo.findById(dto.getMarcheId())
                .orElseThrow(() -> new RuntimeException("Marché introuvable avec l'ID: " + dto.getMarcheId()));

        PenalitePredictionDTO prediction = calculerPrediction(marche, dto.getTauxReel());

        Avancement avancement = Avancement.builder()
                .marche(marche)
                .tauxPrevu(prediction.getAvancementPrevuPct())
                .tauxReel(dto.getTauxReel())
                .description(dto.getDescription())
                .dateAvancement(LocalDate.now())
                .build();

        if (fichiers != null && !fichiers.isEmpty()) {
            enregistrerFichiers(fichiers, avancement);
        }

        Avancement saved = avancementRepo.save(avancement);
        AvancementResponseDTO response = mapToDTO(saved);
        response.setPredictionPenalite(prediction);
        return response;
    }

    @Override
    @Transactional
    public AvancementResponseDTO modifierAvancement(
            Long id,
            AvancementCreateDTO dto,
            List<MultipartFile> nouveauxFichiers,
            List<Long> idsFichiersASupprimer) {

        Avancement avancement = avancementRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Avancement introuvable avec l'ID: " + id));

        Marche marche = avancement.getMarche();

        // 1. Mise à jour des données textuelles et recalcul du prévisionnel
        avancement.setTauxReel(dto.getTauxReel());
        avancement.setDescription(dto.getDescription());

        PenalitePredictionDTO prediction = calculerPrediction(marche, dto.getTauxReel());
        avancement.setTauxPrevu(prediction.getAvancementPrevuPct());

        // 2. Suppression des fichiers sélectionnés
        if (idsFichiersASupprimer != null && !idsFichiersASupprimer.isEmpty()) {
            avancement.getPiecesJointes().removeIf(pj -> {
                if (idsFichiersASupprimer.contains(pj.getId())) {
                    try {
                        Files.deleteIfExists(Paths.get(pj.getCheminFichier()));
                    } catch (IOException e) {
                        System.err.println("Erreur de suppression physique : " + pj.getCheminFichier());
                    }
                    return true;
                }
                return false;
            });
        }

        // 3. Enregistrement des nouveaux fichiers s'il y en a
        if (nouveauxFichiers != null && !nouveauxFichiers.isEmpty()) {
            enregistrerFichiers(nouveauxFichiers, avancement);
        }

        Avancement updated = avancementRepo.save(avancement);
        AvancementResponseDTO response = mapToDTO(updated);
        response.setPredictionPenalite(prediction);
        return response;
    }

    @Override
    public List<AvancementResponseDTO> getHistoriqueByMarche(Long marcheId) {
        return avancementRepo.findByMarcheIdOrderByDateAvancementDesc(marcheId)
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void supprimerAvancement(Long id) {
        Avancement avancement = avancementRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Avancement introuvable avec l'ID: " + id));

        // Suppression physique des fichiers du serveur
        if (avancement.getPiecesJointes() != null) {
            for (PieceJointeAvancement pj : avancement.getPiecesJointes()) {
                try {
                    Files.deleteIfExists(Paths.get(pj.getCheminFichier()));
                } catch (IOException e) {
                    System.err.println("Impossible de supprimer le fichier: " + pj.getCheminFichier());
                }
            }
        }

        avancementRepo.delete(avancement);
    }

    @Override
    public PenalitePredictionDTO simulerPredictionPenalites(Long marcheId) {
        Marche marche = marcheRepo.findById(marcheId)
                .orElseThrow(() -> new RuntimeException("Marché introuvable avec l'ID: " + marcheId));

        BigDecimal dernierTauxReel = avancementRepo.findFirstByMarcheIdOrderByDateAvancementDescCreatedAtDesc(marcheId)
                .map(Avancement::getTauxReel)
                .orElse(BigDecimal.ZERO);

        return calculerPrediction(marche, dernierTauxReel);
    }

    @Override
    public List<AvancementResponseDTO> rechercherParMotCle(Long marcheId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getHistoriqueByMarche(marcheId);
        }

        return avancementRepo.findByMarche_IdAndDescriptionContainingIgnoreCaseOrderByDateAvancementDesc(marcheId, keyword.trim())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- Méthodes privées d'aide ---

    private void enregistrerFichiers(List<MultipartFile> fichiers, Avancement avancement) {
        String uploadDir = "uploads/avancements/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        for (MultipartFile file : fichiers) {
            if (!file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);
                try {
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    PieceJointeAvancement pj = PieceJointeAvancement.builder()
                            .nomFichier(fileName)
                            .cheminFichier(filePath.toString())
                            .typeFichier(file.getContentType())
                            .avancement(avancement)
                            .build();

                    avancement.getPiecesJointes().add(pj);
                } catch (IOException e) {
                    throw new RuntimeException("Erreur de sauvegarde du fichier: " + fileName, e);
                }
            }
        }
    }

    private PenalitePredictionDTO calculerPrediction(Marche marche, BigDecimal tauxReel) {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate dateDebut = marche.getDateNotificationOs() != null ? marche.getDateNotificationOs() : aujourdhui;

        long totalJoursExec = marche.getDelaiExecutionDays() != null ? marche.getDelaiExecutionDays() : 1;
        if (totalJoursExec <= 0) totalJoursExec = 1;

        // Date de fin prévisionnelle du marché
        LocalDate dateFinPrevue = marche.getDateFinPrevue() != null
                ? marche.getDateFinPrevue()
                : dateDebut.plusDays(totalJoursExec);

        long joursEcoules = ChronoUnit.DAYS.between(dateDebut, aujourdhui);
        if (joursEcoules < 0) joursEcoules = 0;

        double tauxPrevuCalcul = Math.min(100.0, ((double) joursEcoules / totalJoursExec) * 100.0);
        BigDecimal avancementPrevu = BigDecimal.valueOf(tauxPrevuCalcul).setScale(2, RoundingMode.HALF_UP);

        BigDecimal ecart = avancementPrevu.subtract(tauxReel != null ? tauxReel : BigDecimal.ZERO);

        // 💡 MODIFICATION : Calcul exact des jours de calendrier dépassés par rapport à aujourd'hui
        long joursRetardEstimes = 0;
        if (aujourdhui.isAfter(dateFinPrevue)) {
            joursRetardEstimes = ChronoUnit.DAYS.between(dateFinPrevue, aujourdhui);
        }

        BigDecimal budget = marche.getBudget() != null ? marche.getBudget() : BigDecimal.ZERO;
        BigDecimal tauxPenaliteJour = marche.getTauxPenaliteJour() != null ? marche.getTauxPenaliteJour() : new BigDecimal("0.001");

        BigDecimal penaliteBrute = budget.multiply(tauxPenaliteJour).multiply(BigDecimal.valueOf(joursRetardEstimes));

        BigDecimal pctPlafond = marche.getPlafondPenalitePct() != null ? marche.getPlafondPenalitePct() : new BigDecimal("10.00");
        BigDecimal montantPlafondMax = budget.multiply(pctPlafond.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));

        boolean plafondAtteint = penaliteBrute.compareTo(montantPlafondMax) >= 0;
        BigDecimal montantPenaliteFinal = plafondAtteint ? montantPlafondMax : penaliteBrute;

        boolean alertePrioritaire = montantPenaliteFinal.compareTo(montantPlafondMax.multiply(new BigDecimal("0.80"))) >= 0
                && montantPlafondMax.compareTo(BigDecimal.ZERO) > 0;

        return PenalitePredictionDTO.builder()
                .marcheId(marche.getId())
                .numMarche(marche.getNumMarche())
                .totalJoursExec(totalJoursExec)
                .joursEcoules(joursEcoules)
                .avancementPrevuPct(avancementPrevu)
                .avancementReelPct(tauxReel)
                .ecartPct(ecart)
                .joursRetardEstimes(joursRetardEstimes)
                .montantBudget(budget)
                .montantPenaliteEstime(montantPenaliteFinal.setScale(2, RoundingMode.HALF_UP))
                .montantPlafondMax(montantPlafondMax.setScale(2, RoundingMode.HALF_UP))
                .plafondAtteint(plafondAtteint)
                .alertePrioritaire(alertePrioritaire)
                .build();
    }

    private AvancementResponseDTO mapToDTO(Avancement entity) {
        AvancementResponseDTO dto = new AvancementResponseDTO();
        dto.setId(entity.getId());
        dto.setMarcheId(entity.getMarche().getId());
        dto.setNumMarche(entity.getMarche().getNumMarche());
        dto.setTauxPrevu(entity.getTauxPrevu());
        dto.setTauxReel(entity.getTauxReel());
        dto.setDescription(entity.getDescription());
        dto.setDateAvancement(entity.getDateAvancement());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getPiecesJointes() != null) {
            dto.setPiecesJointes(entity.getPiecesJointes().stream().map(pj ->
                    new PieceJointeDTO(
                            pj.getId(),
                            pj.getNomFichier(),
                            pj.getTypeFichier(),
                            "/api/avancements/fichiers/" + pj.getNomFichier()
                    )
            ).collect(Collectors.toList()));
        }

        return dto;
    }
    @Override
    public List<PenalitePredictionDTO> getToutesLesAlertesPenalites() {
        List<Marche> marchesActifs = marcheRepo.findByActifTrue(); // ou findAll()

        return marchesActifs.stream()
                .map(marche -> {
                    BigDecimal dernierTauxReel = avancementRepo
                            .findFirstByMarcheIdOrderByDateAvancementDescCreatedAtDesc(marche.getId())
                            .map(Avancement::getTauxReel)
                            .orElse(BigDecimal.ZERO);
                    return calculerPrediction(marche, dernierTauxReel);
                })
                // Filtrer uniquement les marchés qui ont un retard (ecart > 0)
                .filter(pred -> pred.getEcartPct().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());
    }
}