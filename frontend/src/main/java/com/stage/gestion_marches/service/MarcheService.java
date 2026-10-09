package com.stage.gestion_marches.service;
import com.stage.gestion_marches.dto.MarcheDTO;
import com.stage.gestion_marches.entity.Marche;
import com.stage.gestion_marches.entity.Prestataire;
import com.stage.gestion_marches.repository.MarcheRepository;
import com.stage.gestion_marches.repository.PrestataireRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarcheService {

    private final MarcheRepository marcheRepository;
    private final PrestataireRepository prestataireRepository;

    public List<MarcheDTO> getAll() {
        return marcheRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public void toggleActif(Long id) {
        Marche marche = marcheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marché introuvable avec l'ID : " + id));

        // Si actif est null, on le met à false, sinon on inverse la valeur actuelle
        boolean nouveauStatut = (marche.getActif() == null) ? false : !marche.getActif();
        marche.setActif(nouveauStatut);

        marcheRepository.save(marche);
    }
    public MarcheDTO getById(Long id) {
        Marche m = marcheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marché introuvable avec l'ID: " + id));
        return toDTO(m);
    }

    public MarcheDTO create(MarcheDTO dto) {
        Marche marche = toEntity(dto);
        return toDTO(marcheRepository.save(marche));
    }

    public MarcheDTO update(Long id, MarcheDTO dto) {
        Marche existing = marcheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marché introuvable"));

        existing.setNumMarche(dto.getNumMarche());
        existing.setModePassation(dto.getModePassation());
        existing.setObjetMarche(dto.getObjetMarche());
        existing.setDateApprobation(dto.getDateApprobation());
        existing.setDateFinPrevue(dto.getDateFinPrevue());
        existing.setVisaNumero(dto.getVisaNumero());
        existing.setExercice(dto.getExercice());
        existing.setBudget(dto.getBudget());
        existing.setArticle(dto.getArticle());
        existing.setParagraphe(dto.getParagraphe());
        existing.setLigne(dto.getLigne());
        existing.setRubrique(dto.getRubrique());

        if (dto.getPrestataireId() != null) {
            Prestataire p = prestataireRepository.findById(dto.getPrestataireId()).orElse(null);
            existing.setPrestataire(p);
        }

        return toDTO(marcheRepository.save(existing));
    }

    public void delete(Long id) {
        marcheRepository.deleteById(id);
    }

    // Recherche par mot-clé
    public List<MarcheDTO> search(String keyword, String exercice) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return marcheRepository
                .findByNumMarcheContainingIgnoreCaseOrObjetMarcheContainingIgnoreCaseOrPrestataireNomSocieteContainingIgnoreCase(keyword, keyword, keyword)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Option Intelligente : Alerte échéance
    public List<MarcheDTO> getAlertesEcheance(int daysThreshold) {
        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(daysThreshold);

        return marcheRepository
                .findByActifTrueAndDateFinPrevueBetween(today, limitDate)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    private MarcheDTO toDTO(Marche m) {
        MarcheDTO dto = new MarcheDTO();
        dto.setId(m.getId());
        dto.setNumMarche(m.getNumMarche());
        dto.setModePassation(m.getModePassation());
        dto.setObjetMarche(m.getObjetMarche());
        dto.setDateApprobation(m.getDateApprobation());
        dto.setDateFinPrevue(m.getDateFinPrevue());
        dto.setVisaNumero(m.getVisaNumero());
        dto.setExercice(m.getExercice());
        dto.setBudget(m.getBudget());
        dto.setArticle(m.getArticle());
        dto.setParagraphe(m.getParagraphe());
        dto.setLigne(m.getLigne());
        dto.setRubrique(m.getRubrique());
        dto.setActif(m.getActif());

        if (m.getPrestataire() != null) {
            dto.setPrestataireId(m.getPrestataire().getId());
            dto.setNomPrestataire(m.getPrestataire().getNomSociete());
        }
        return dto;
    }

    private Marche toEntity(MarcheDTO dto) {
        Marche m = new Marche();
        m.setNumMarche(dto.getNumMarche());
        m.setModePassation(dto.getModePassation());
        m.setObjetMarche(dto.getObjetMarche());
        m.setDateApprobation(dto.getDateApprobation());
        m.setDateFinPrevue(dto.getDateFinPrevue());
        m.setVisaNumero(dto.getVisaNumero());
        m.setExercice(dto.getExercice());
        m.setBudget(dto.getBudget());
        m.setArticle(dto.getArticle());
        m.setParagraphe(dto.getParagraphe());
        m.setLigne(dto.getLigne());
        m.setRubrique(dto.getRubrique());

        if (dto.getPrestataireId() != null) {
            Prestataire p = prestataireRepository.findById(dto.getPrestataireId()).orElse(null);
            m.setPrestataire(p);
        }
        return m;
    }
}