package com.stage.gestion_marches.service;

import com.stage.gestion_marches.dto.NantissementDTO;
import com.stage.gestion_marches.entity.Marche;
import com.stage.gestion_marches.entity.Nantissement;
import com.stage.gestion_marches.repository.MarcheRepository;
import com.stage.gestion_marches.repository.NantissementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NantissementService {

    private final NantissementRepository nantissementRepository;
    private final MarcheRepository marcheRepository;

    @Transactional(readOnly = true)
    public NantissementDTO getByMarcheId(Long marcheId) {
        Nantissement nantissement = nantissementRepository.findByMarcheId(marcheId)
                .orElseThrow(() -> new RuntimeException("Nantissement non trouvé pour le marché ID : " + marcheId));
        return mapToDTO(nantissement);
    }
    @Transactional(readOnly = true)
    public List<NantissementDTO> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return nantissementRepository.findAll()
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }

        return nantissementRepository.searchNantissements(keyword.trim())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public NantissementDTO saveOrUpdate(Long marcheId, NantissementDTO dto) {
        Marche marche = marcheRepository.findById(marcheId)
                .orElseThrow(() -> new RuntimeException("Marché non trouvé avec l'ID : " + marcheId));

        Nantissement nantissement = nantissementRepository.findByMarcheId(marcheId)
                .orElse(new Nantissement());

        // Rattachement au marché
        nantissement.setMarche(marche);

        // Informations bancaires
        nantissement.setCompteBancaire(dto.getCompteBancaire());
        nantissement.setBanque(dto.getBanque());

        // Montants engagés
        nantissement.setMontantInitial(dto.getMontantInitial());
        nantissement.setInteretsMoratoires(dto.getInteretsMoratoires());
        nantissement.setSommeAValoire(dto.getSommeAValoire());
        nantissement.setCp(dto.getCp());
        nantissement.setCe(dto.getCe());

        // Caution Définitive
        nantissement.setMontantCautionDefinitive(dto.getMontantCautionDefinitive());
        nantissement.setDateConstitutionCaution(dto.getDateConstitutionCaution());
        nantissement.setDateLiberationCaution(dto.getDateLiberationCaution());

        // Retenue de Garantie
        nantissement.setMontantRetenueGarantie(dto.getMontantRetenueGarantie());
        nantissement.setDateConstitutionRetenue(dto.getDateConstitutionRetenue());
        nantissement.setDateLiberationRetenue(dto.getDateLiberationRetenue());

        // Sauvegarde (totalEngage calculé automatiquement par @PrePersist / @PreUpdate)
        Nantissement saved = nantissementRepository.save(nantissement);
        return mapToDTO(saved);
    }

    @Transactional
    public void deleteByMarcheId(Long marcheId) {
        if (!marcheRepository.existsById(marcheId)) {
            throw new RuntimeException("Impossible de supprimer le nantissement. Marché introuvable ID : " + marcheId);
        }
        nantissementRepository.deleteByMarcheId(marcheId);
    }

    private NantissementDTO mapToDTO(Nantissement entity) {
        return NantissementDTO.builder()
                .id(entity.getId())
                .marcheId(entity.getMarche() != null ? entity.getMarche().getId() : null)
                .compteBancaire(entity.getCompteBancaire())
                .banque(entity.getBanque())
                .montantInitial(entity.getMontantInitial())
                .interetsMoratoires(entity.getInteretsMoratoires())
                .sommeAValoire(entity.getSommeAValoire())
                .totalEngage(entity.getTotalEngage())
                .cp(entity.getCp())
                .ce(entity.getCe())
                .montantCautionDefinitive(entity.getMontantCautionDefinitive())
                .dateConstitutionCaution(entity.getDateConstitutionCaution())
                .dateLiberationCaution(entity.getDateLiberationCaution())
                .montantRetenueGarantie(entity.getMontantRetenueGarantie())
                .dateConstitutionRetenue(entity.getDateConstitutionRetenue())
                .dateLiberationRetenue(entity.getDateLiberationRetenue())
                .build();
    }
}