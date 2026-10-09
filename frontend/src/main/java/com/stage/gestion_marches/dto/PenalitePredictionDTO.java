package com.stage.gestion_marches.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenalitePredictionDTO {
    private Long marcheId;
    private String numMarche;

    // Chrono & Avancement
    private long totalJoursExec;
    private long joursEcoules;
    private BigDecimal avancementPrevuPct;
    private BigDecimal avancementReelPct;
    private BigDecimal ecartPct; // Prevu - Reel

    // Prédictions Pénalités
    private long joursRetardEstimes;
    private BigDecimal montantBudget;
    private BigDecimal montantPenaliteEstime;
    private BigDecimal montantPlafondMax; // Ex: 10% du budget
    private boolean plafondAtteint;
    private boolean alertePrioritaire; // Vrai si les pénalités dépassent 80% du plafond
}