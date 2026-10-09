package com.stage.gestion_marches.dto;

import com.stage.gestion_marches.entity.EtatProbleme;
import com.stage.gestion_marches.entity.Priorite;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class PieceJointeResponseDTO {
    private Long id;
    private String nomFichier;
    private String cheminFichier;
    private String typeFichier;
    private LocalDateTime dateDepot;
}