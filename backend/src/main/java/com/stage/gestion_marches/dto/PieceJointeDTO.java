package com.stage.gestion_marches.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PieceJointeDTO {
    private Long id;
    private String nomFichier;
    private String typeFichier;
    private String url;
}