package com.stage.gestion_marches.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prestataires")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestataire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_societe", nullable = false)
    private String nomSociete;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    private String rc;
    private String cnss;
    private String patente;

    @Column(name = "identifiant_fiscal")
    private String identifiantFiscal;

    private String banque;

    @Column(name = "cle_rib")
    private String cle_rib;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;
}