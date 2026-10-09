package com.stage.gestion_marches.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "marches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Marche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num_marche", unique = true, nullable = false, length = 100)
    private String numMarche;

    @Column(name = "mode_passation")
    private String modePassation;

    @Column(name = "objet_marche", nullable = false, columnDefinition = "TEXT")
    private String objetMarche;

    @Column(name = "date_approbation")
    private LocalDate dateApprobation;

    @Column(name = "date_fin_prevue")
    private LocalDate dateFinPrevue;

    @Column(name = "visa_numero", length = 100)
    private String visaNumero;

    // --- Champs Chrono & Pénalités ---
    @Column(name = "date_notification_os")
    private LocalDate dateNotificationOs;

    @Column(name = "delai_execution_days")
    private Long delaiExecutionDays;

    @Column(name = "taux_penalite_jour", precision = 6, scale = 4)
    private BigDecimal tauxPenaliteJour;

    @Column(name = "plafond_penalite_pct", precision = 5, scale = 2)
    private BigDecimal plafondPenalitePct;

    // --- Champs budgétaires ---
    private String exercice;
    @Column(precision = 15, scale = 2)
    private BigDecimal budget;
    private String article;
    private String paragraphe;
    private String ligne;
    private String rubrique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestataire_id", foreignKey = @ForeignKey(name = "fk_marche_prestataire"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "marches"})
    private Prestataire prestataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imputation_id", foreignKey = @ForeignKey(name = "fk_marche_imputation"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "marches"})
    private ImputationBudgetaire imputationBudgetaire;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean actif = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}