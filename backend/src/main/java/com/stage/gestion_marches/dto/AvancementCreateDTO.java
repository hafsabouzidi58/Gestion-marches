package com.stage.gestion_marches.dto;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AvancementCreateDTO {
    @NotNull(message = "L'ID du marché est obligatoire")
    private Long marcheId;

    @NotNull(message = "Le taux réel est obligatoire")
    @DecimalMin(value = "0.0", message = "Le taux ne peut pas être inférieur à 0")
    @DecimalMax(value = "100.0", message = "Le taux ne peut pas dépasser 100")
    private BigDecimal tauxReel;

    private String description;
}