package com.stage.gestion_marches.service;
import com.stage.gestion_marches.dto.PrestataireDTO;
import java.util.List;

public interface PrestataireService {
    List<PrestataireDTO> getAllPrestataires();
    List<PrestataireDTO> getActivePrestataires();
    PrestataireDTO getPrestataireById(Long id);
    PrestataireDTO createPrestataire(PrestataireDTO dto);
    PrestataireDTO updatePrestataire(Long id, PrestataireDTO dto);
    void deletePrestataire(Long id);
    void toggleActif(Long id);
}