package com.stage.gestion_marches.model;

import com.stage.gestion_marches.entity.Marche;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "decomptes")
public class Decompte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num_decompte", nullable = false)
    private String numDecompte;

    @Column(name = "montant_brut", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantBrut;

    @Column(name = "retenue_garantie", precision = 15, scale = 2)
    private BigDecimal retenueGarantie;

    @Column(name = "montant_net", precision = 15, scale = 2)
    private BigDecimal montantNet;

    @Column(name = "date_decompte", nullable = false)
    private LocalDate dateDecompte;

    @Column(name = "est_paye", nullable = false)
    private Boolean estPaye = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marche_id", nullable = false)
    private Marche marche;

    public Decompte() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumDecompte() { return numDecompte; }
    public void setNumDecompte(String numDecompte) { this.numDecompte = numDecompte; }

    public BigDecimal getMontantBrut() { return montantBrut; }
    public void setMontantBrut(BigDecimal montantBrut) { this.montantBrut = montantBrut; }

    public BigDecimal getRetenueGarantie() { return retenueGarantie; }
    public void setRetenueGarantie(BigDecimal retenueGarantie) { this.retenueGarantie = retenueGarantie; }

    public BigDecimal getMontantNet() { return montantNet; }
    public void setMontantNet(BigDecimal montantNet) { this.montantNet = montantNet; }

    public LocalDate getDateDecompte() { return dateDecompte; }
    public void setDateDecompte(LocalDate dateDecompte) { this.dateDecompte = dateDecompte; }

    public Boolean getEstPaye() { return estPaye; }
    public void setEstPaye(Boolean estPaye) { this.estPaye = estPaye; }

    public Marche getMarche() { return marche; }
    public void setMarche(Marche marche) { this.marche = marche; }
}