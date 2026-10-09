package com.stage.gestion_marches.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NantissementDTO {

    private Long id;
    private Long marcheId;
    private String compteBancaire;
    private String banque;

    private BigDecimal montantInitial;
    private BigDecimal interetsMoratoires;
    private BigDecimal sommeAValoire;
    private BigDecimal totalEngage;
    private BigDecimal cp;
    private BigDecimal ce;

    // Caution Définitive
    private BigDecimal montantCautionDefinitive;
    private LocalDate dateConstitutionCaution;
    private LocalDate dateLiberationCaution;

    // Retenue de Garantie
    private BigDecimal montantRetenueGarantie;
    private String dateConstitutionRetenue;
    private LocalDate dateLiberationRetenue;
}