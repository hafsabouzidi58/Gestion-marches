package com.stage.gestion_marches.service;
import com.stage.gestion_marches.dto.PrestataireDTO;
import com.stage.gestion_marches.entity.Prestataire;
import com.stage.gestion_marches.repository.PrestataireRepository;
import com.stage.gestion_marches.service.PrestataireService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrestataireServiceImpl implements PrestataireService {

    private final PrestataireRepository prestataireRepository;

    @Override
    public List<PrestataireDTO> getAllPrestataires() {
        return prestataireRepository.findAllByOrderByIdAsc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrestataireDTO> getActivePrestataires() {
        return prestataireRepository.findByActifTrueOrderByIdAsc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PrestataireDTO getPrestataireById(Long id) {
        Prestataire prestataire = prestataireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestataire non trouvé avec l'ID : " + id));
        return mapToDTO(prestataire);
    }

    @Override
    public PrestataireDTO createPrestataire(PrestataireDTO dto) {
        Prestataire prestataire = mapToEntity(dto);

        // ⚠️ Assurez-vous que le RIB est bien pris en compte
        prestataire.setCle_rib(dto.getCle_rib());
        prestataire.setActif(true);

        Prestataire saved = prestataireRepository.save(prestataire);
        return mapToDTO(saved);
    }

    @Override
    public PrestataireDTO updatePrestataire(Long id, PrestataireDTO dto) {
        Prestataire existing = prestataireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestataire non trouvé avec l'ID : " + id));

        existing.setNomSociete(dto.getNomSociete());
        existing.setAdresse(dto.getAdresse());
        existing.setRc(dto.getRc());
        existing.setCnss(dto.getCnss());
        existing.setPatente(dto.getPatente());
        existing.setIdentifiantFiscal(dto.getIdentifiantFiscal());
        existing.setBanque(dto.getBanque());
        existing.setCle_rib(dto.getCle_rib());

        return mapToDTO(prestataireRepository.save(existing));
    }

    @Override
    public void deletePrestataire(Long id) {
        prestataireRepository.deleteById(id);
    }

    @Override
    public void toggleActif(Long id) {
        Prestataire p = prestataireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestataire non trouvé"));
        p.setActif(!p.getActif());
        prestataireRepository.save(p);
    }

    // Mapping Entity <-> DTO
    private PrestataireDTO mapToDTO(Prestataire p) {
        return PrestataireDTO.builder()
                .id(p.getId())
                .nomSociete(p.getNomSociete())
                .adresse(p.getAdresse())
                .rc(p.getRc())
                .cnss(p.getCnss())
                .patente(p.getPatente())
                .identifiantFiscal(p.getIdentifiantFiscal())
                .banque(p.getBanque())
                .cle_rib(p.getCle_rib())
                .actif(p.getActif())
                .build();
    }

    private Prestataire mapToEntity(PrestataireDTO dto) {
        return Prestataire.builder()
                .id(dto.getId())
                .nomSociete(dto.getNomSociete())
                .adresse(dto.getAdresse())
                .rc(dto.getRc())
                .cnss(dto.getCnss())
                .patente(dto.getPatente())
                .identifiantFiscal(dto.getIdentifiantFiscal())
                .banque(dto.getBanque())
                .cle_rib(dto.getCle_rib())
                .actif(dto.getActif() != null ? dto.getActif() : true)
                .build();
    }
}
