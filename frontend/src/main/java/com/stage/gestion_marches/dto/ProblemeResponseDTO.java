package com.stage.gestion_marches.dto;

import com.stage.gestion_marches.entity.EtatProbleme;
import com.stage.gestion_marches.entity.Priorite;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProblemeResponseDTO {
    private Long id;
    private String titre;
    private Long marcheId;
    private String numMarche;
    private String description;
    private EtatProbleme etat;
    private Priorite priorite;
    private Long declareParId;
    private String declareParNom;
    private LocalDateTime dateDeclaration;
    private LocalDateTime dateResolution;
    private List<MessageResponseDTO> messages;
    private List<PieceJointeResponseDTO> piecesJointesDeclaration;
}