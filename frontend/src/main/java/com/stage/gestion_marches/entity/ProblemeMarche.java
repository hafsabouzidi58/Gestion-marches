package com.stage.gestion_marches.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "problemes_marches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemeMarche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre; // Ex: "Retard de livraison du lot N°2"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marche_id", nullable = false)
    private Marche marche;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "etat")
    private EtatProbleme etat;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "priorite")
    private Priorite priorite;

    // Utilisateur qui a déclaré le problème (ex: Responsable Green Wood ou Prestataire)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "declare_par_id")
    private Utilisateur declarePar;

    @Column(name = "date_declaration", updatable = false)
    @Builder.Default
    private LocalDateTime dateDeclaration = LocalDateTime.now();

    @Column(name = "date_resolution")
    private LocalDateTime dateResolution;

    // Fil de discussion et échanges de documents
    @OneToMany(mappedBy = "probleme", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MessageProbleme> messages = new ArrayList<>();
}