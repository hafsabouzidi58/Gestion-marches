package com.stage.gestion_marches.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pieces_jointes_problemes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceJointeProbleme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private MessageProbleme message;

    @Column(nullable = false)
    private String nomFichier; // Ex: "rapport_incident_v1.pdf"

    @Column(nullable = false)
    private String cheminFichier; // Chemin sur le serveur (ex: "/uploads/problems/123.pdf")

    private String typeFichier; // Ex: "application/pdf", "image/png"

    @Column(name = "date_depot", updatable = false)
    @Builder.Default
    private LocalDateTime dateDepot = LocalDateTime.now();
}