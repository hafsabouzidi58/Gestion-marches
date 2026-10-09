package com.stage.gestion_marches.service;

import com.stage.gestion_marches.dto.AvancementCreateDTO;
import com.stage.gestion_marches.dto.AvancementResponseDTO;
import com.stage.gestion_marches.dto.PenalitePredictionDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AvancementService {

    AvancementResponseDTO enregistrerAvancement(AvancementCreateDTO dto, List<MultipartFile> fichiers);

    AvancementResponseDTO modifierAvancement(Long id, AvancementCreateDTO dto, List<MultipartFile> nouveauxFichiers, List<Long> idsFichiersASupprimer);

    List<AvancementResponseDTO> getHistoriqueByMarche(Long marcheId);

    PenalitePredictionDTO simulerPredictionPenalites(Long marcheId);

    List<AvancementResponseDTO> rechercherParMotCle(Long marcheId, String keyword);

    void supprimerAvancement(Long id);

    // Méthode pour le Dashboard (Alertes globales)
    List<PenalitePredictionDTO> getToutesLesAlertesPenalites();
}