package com.stage.gestion_marches.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "nantissements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nantissement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marche_id")
    private Marche marche;

    @Column(name = "compte_bancaire")
    private String compteBancaire;

    private String banque;

    // --- MONTANTS ENGAGÉS ---
    @Column(name = "montant_initial", precision = 15, scale = 2)
    private BigDecimal montantInitial;

    @Column(name = "interets_moratoires", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal interetsMoratoires = BigDecimal.ZERO;

    @Column(name = "somme_a_valoire", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal sommeAValoire = BigDecimal.ZERO;

    @Column(name = "total_engage", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalEngage = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal cp = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal ce = BigDecimal.ZERO;

    // --- CAUTION DÉFINITIVE ---
    @Column(name = "montant_caution_definitive", precision = 15, scale = 2)
    private BigDecimal montantCautionDefinitive;

    @Column(name = "date_constitution_caution")
    private LocalDate dateConstitutionCaution;

    @Column(name = "date_liberation_caution")
    private LocalDate dateLiberationCaution;

    // --- RETENUE DE GARANTIE ---
    @Column(name = "montant_retenue_garantie", precision = 15, scale = 2)
    private BigDecimal montantRetenueGarantie;

    @Column(name = "date_constitution_retenue")
    private String dateConstitutionRetenue; // Ex: "Sur DP"

    @Column(name = "date_liberation_retenue")
    private LocalDate dateLiberationRetenue;

    @PrePersist
    @PreUpdate
    public void calculerTotalEngage() {
        BigDecimal mi = (montantInitial != null) ? montantInitial : BigDecimal.ZERO;
        BigDecimal im = (interetsMoratoires != null) ? interetsMoratoires : BigDecimal.ZERO;
        BigDecimal sav = (sommeAValoire != null) ? sommeAValoire : BigDecimal.ZERO;

        this.totalEngage = mi.add(im).add(sav);
        if (this.cp == null || this.cp.compareTo(BigDecimal.ZERO) == 0) {
            this.cp = this.totalEngage;
        }
    }
}