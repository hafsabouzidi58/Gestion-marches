package com.stage.gestion_marches.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages_problemes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageProbleme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "probleme_id", nullable = false)
    private ProblemeMarche probleme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id", nullable = false)
    private Utilisateur auteur;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "date_envoi", updatable = false)
    @Builder.Default
    private LocalDateTime dateEnvoi = LocalDateTime.now();

    // Pièces jointes (photos, PV, documents)
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PieceJointeProbleme> piecesJointes = new ArrayList<>();
}