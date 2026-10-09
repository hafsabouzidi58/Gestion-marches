package com.stage.gestion_marches.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MessageResponseDTO {
    private Long id;
    private Long auteurId;
    private String auteurNom;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private List<PieceJointeResponseDTO> piecesJointes;
}