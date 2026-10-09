package com.stage.gestion_marches.dto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MarcheDTO {
    private Long id;
    private String numMarche;
    private String modePassation;
    private String objetMarche;
    private LocalDate dateApprobation;
    private LocalDate dateFinPrevue;
    private String visaNumero;
    private String exercice;
    private BigDecimal budget;
    private String article;
    private String paragraphe;
    private String ligne;
    private String rubrique;
    private Long prestataireId;
    private String nomPrestataire;
    private Long imputationId;
    private Boolean actif;
}