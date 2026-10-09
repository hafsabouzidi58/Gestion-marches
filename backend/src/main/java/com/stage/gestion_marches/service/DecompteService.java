package com.stage.gestion_marches.service;

import com.stage.gestion_marches.dto.DecompteDTO;
import com.stage.gestion_marches.model.Decompte;
import com.stage.gestion_marches.entity.Marche;
import com.stage.gestion_marches.repository.DecompteRepository;
import com.stage.gestion_marches.repository.MarcheRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DecompteService {

    private final DecompteRepository decompteRepository;
    private final MarcheRepository marcheRepository;

    public DecompteService(DecompteRepository decompteRepository, MarcheRepository marcheRepository) {
        this.decompteRepository = decompteRepository;
        this.marcheRepository = marcheRepository;
    }

    // Calcul du budget restant sur le marché
    public BigDecimal calculerSoldeRestantMarche(Long marcheId) {
        Marche marche = marcheRepository.findById(marcheId)
                .orElseThrow(() -> new EntityNotFoundException("Marché introuvable avec l'ID: " + marcheId));

        BigDecimal totalConsomme = decompteRepository.sumMontantBrutByMarcheId(marcheId);

        // S'assurer de remplacer null par ZERO
        if (totalConsomme == null) {
            totalConsomme = BigDecimal.ZERO;
        }

        // Gestion du type budget (BigDecimal ou String)
        BigDecimal budgetMarche = (marche.getBudget() != null) ? new BigDecimal(marche.getBudget().toString()) : BigDecimal.ZERO;

        BigDecimal solde = budgetMarche.subtract(totalConsomme);
        return solde.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : solde;
    }
    @Transactional
    public DecompteDTO enregistrerDecompte(DecompteDTO dto) {
        Marche marche = marcheRepository.findById(dto.getMarcheId())
                .orElseThrow(() -> new EntityNotFoundException("Marché introuvable"));

        BigDecimal soldeDisponible = calculerSoldeRestantMarche(dto.getMarcheId());

        // Vérification : Le décompte ne peut pas dépasser le budget disponible
        if (dto.getMontantBrut().compareTo(soldeDisponible) > 0) {
            throw new IllegalArgumentException("Le montant brut (" + dto.getMontantBrut()
                    + " DH) dépasse le solde disponible du marché (" + soldeDisponible + " DH)");
        }

        // Calcul automatique de la Retenue de Garantie (10%) et du Net à Payer
        BigDecimal retenue = dto.getMontantBrut().multiply(new BigDecimal("0.10"));
        BigDecimal net = dto.getMontantBrut().subtract(retenue);

        Decompte decompte = new Decompte();
        decompte.setNumDecompte(dto.getNumDecompte());
        decompte.setDateDecompte(dto.getDateDecompte());
        decompte.setMontantBrut(dto.getMontantBrut());
        decompte.setRetenueGarantie(retenue);
        decompte.setMontantNet(net);
        decompte.setEstPaye(dto.getEstPaye() != null ? dto.getEstPaye() : false);
        decompte.setMarche(marche);

        Decompte saved = decompteRepository.save(decompte);
        return mapToDTO(saved);
    }

    public List<DecompteDTO> getDecomptesByMarche(Long marcheId) {
        return decompteRepository.findByMarcheId(marcheId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private DecompteDTO mapToDTO(Decompte entity) {
        DecompteDTO dto = new DecompteDTO();
        dto.setId(entity.getId());
        dto.setNumDecompte(entity.getNumDecompte());
        dto.setDateDecompte(entity.getDateDecompte());
        dto.setMontantBrut(entity.getMontantBrut());
        dto.setRetenueGarantie(entity.getRetenueGarantie());
        dto.setMontantNet(entity.getMontantNet());
        dto.setEstPaye(entity.getEstPaye());
        dto.setMarcheId(entity.getMarche().getId());
        return dto;
    }
}