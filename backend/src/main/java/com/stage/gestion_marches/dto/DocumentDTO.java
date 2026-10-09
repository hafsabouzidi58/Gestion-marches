package com.stage.gestion_marches.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentDTO {
    private Long id;
    private Long marcheId;
    private Long uploadePar;
    private String nomFichier;
    private String typeDocument;
    private String cheminFichier;
    private Long tailleKo;
    private LocalDateTime createdAt;
}