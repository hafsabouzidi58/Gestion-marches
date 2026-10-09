package com.stage.gestion_marches.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestataireDTO {
    private Long id;
    private String nomSociete;
    private String adresse;
    private String rc;
    private String cnss;
    private String patente;
    private String identifiantFiscal;
    private String banque;
    private String cle_rib;
    private Boolean actif;
}