package com.stage.gestion_marches.dto;

import com.stage.gestion_marches.entity.Priorite;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProblemeCreateDTO {
    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotNull(message = "L'ID du marché est obligatoire")
    private Long marcheId;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    private Priorite priorite = Priorite.MOYENNE;
}