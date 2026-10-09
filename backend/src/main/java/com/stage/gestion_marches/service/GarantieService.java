package com.stage.gestion_marches.service;

import com.stage.gestion_marches.dto.GarantieDTO;
import com.stage.gestion_marches.entity.Garantie;
import com.stage.gestion_marches.entity.Marche;
import com.stage.gestion_marches.repository.GarantieRepository;
import com.stage.gestion_marches.repository.MarcheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GarantieService {

    private final GarantieRepository garantieRepository;
    private final MarcheRepository marcheRepository;

    // --- MAPPERS ---
    private GarantieDTO mapToDTO(Garantie entity) {
        if (entity == null) return null;
        return GarantieDTO.builder()
                .id(entity.getId())
                .marcheId(entity.getMarche() != null ? entity.getMarche().getId() : null)
                .numMarche(entity.getMarche() != null ? entity.getMarche().getNumMarche() : null)
                .typeGarantie(entity.getTypeGarantie())
                .montant(entity.getMontant())
                .dateConstitution(entity.getDateConstitution())
                .dateLiberation(entity.getDateLiberation())
                .statutLiberation(entity.getStatutLiberation())
                .build();
    }

    // --- SERVICES ---

    public List<GarantieDTO> getAllGaranties() {
        return garantieRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public GarantieDTO getGarantieById(Long id) {
        Garantie garantie = garantieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Garantie introuvable avec l'ID : " + id));
        return mapToDTO(garantie);
    }

    public List<GarantieDTO> getGarantiesByMarche(Long marcheId) {
        return garantieRepository.findByMarcheId(marcheId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GarantieDTO saveGarantie(GarantieDTO dto) {
        Marche marche = null;
        if (dto.getMarcheId() != null) {
            marche = marcheRepository.findById(dto.getMarcheId())
                    .orElseThrow(() -> new RuntimeException("Marché introuvable avec l'ID : " + dto.getMarcheId()));
        }

        Garantie entity = Garantie.builder()
                .marche(marche)
                .typeGarantie(dto.getTypeGarantie())
                .montant(dto.getMontant())
                .dateConstitution(dto.getDateConstitution())
                .dateLiberation(dto.getDateLiberation())
                .statutLiberation(dto.getStatutLiberation() != null && !dto.getStatutLiberation().isBlank()
                        ? dto.getStatutLiberation() : "EN_COURS")
                .build();

        Garantie saved = garantieRepository.save(entity);
        return mapToDTO(saved);
    }

    @Transactional
    public GarantieDTO updateGarantie(Long id, GarantieDTO dto) {
        // 1. Charger l'entité existante depuis la BDD (Managed State)
        Garantie existingGarantie = garantieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Garantie introuvable avec l'ID : " + id));

        // 2. Mettre à jour la relation Marche si un marcheId est fourni
        if (dto.getMarcheId() != null) {
            Marche marche = marcheRepository.findById(dto.getMarcheId())
                    .orElseThrow(() -> new RuntimeException("Marché introuvable avec l'ID : " + dto.getMarcheId()));
            existingGarantie.setMarche(marche);
        }

        // 3. Mettre à jour les champs simples
        existingGarantie.setTypeGarantie(dto.getTypeGarantie());
        existingGarantie.setMontant(dto.getMontant());
        existingGarantie.setDateConstitution(dto.getDateConstitution());

        if (dto.getDateLiberation() != null) {
            existingGarantie.setDateLiberation(dto.getDateLiberation());
        }

        if (dto.getStatutLiberation() != null && !dto.getStatutLiberation().isBlank()) {
            existingGarantie.setStatutLiberation(dto.getStatutLiberation());
        }

        // 4. Sauvegarder la référence existante modifiée
        Garantie updated = garantieRepository.save(existingGarantie);
        return mapToDTO(updated);
    }

    @Transactional
    public GarantieDTO libererGarantie(Long id) {
        Garantie garantie = garantieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Garantie introuvable avec l'ID : " + id));

        garantie.setStatutLiberation("LIBEREE");
        garantie.setDateLiberation(LocalDate.now());
        Garantie updated = garantieRepository.save(garantie);
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteGarantie(Long id) {
        garantieRepository.deleteById(id);
    }
}