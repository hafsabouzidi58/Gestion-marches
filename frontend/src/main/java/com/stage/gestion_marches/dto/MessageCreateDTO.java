package com.stage.gestion_marches.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageCreateDTO {
    @NotBlank(message = "Le contenu du message ne peut pas être vide")
    private String contenu;
}