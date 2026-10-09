package com.stage.gestion_marches.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AvancementResponseDTO {
    private Long id;
    private Long marcheId;
    private String numMarche;
    private BigDecimal tauxPrevu;
    private BigDecimal tauxReel;
    private String description;
    private LocalDate dateAvancement;
    private LocalDateTime createdAt;
    private PenalitePredictionDTO predictionPenalite;
    private List<PieceJointeDTO> piecesJointes;
}