package com.stage.gestion_marches.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DecompteDTO {

    private Long id;

    @NotNull(message = "Le numéro de décompte est obligatoire")
    private String numDecompte;

    @NotNull(message = "Le montant brut est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant brut doit être supérieur à 0")
    private BigDecimal montantBrut;

    private BigDecimal retenueGarantie;
    private BigDecimal montantNet;

    @NotNull(message = "La date du décompte est obligatoire")
    private LocalDate dateDecompte;

    private Boolean estPaye = false;

    @NotNull(message = "L'ID du marché est obligatoire")
    private Long marcheId;

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

    public Long getMarcheId() { return marcheId; }
    public void setMarcheId(Long marcheId) { this.marcheId = marcheId; }
}