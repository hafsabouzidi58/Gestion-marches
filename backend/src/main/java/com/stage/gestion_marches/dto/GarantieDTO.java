package com.stage.gestion_marches.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stage.gestion_marches.entity.TypeGarantie;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GarantieDTO {

    private Long id;
    private Long marcheId;
    private String numMarche;

    private TypeGarantie typeGarantie;

    private BigDecimal montant;
    private LocalDate dateConstitution;
    private LocalDate dateLiberation;
    private String statutLiberation;
}