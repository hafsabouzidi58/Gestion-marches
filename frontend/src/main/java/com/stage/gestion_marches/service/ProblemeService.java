package com.stage.gestion_marches.service;
import com.stage.gestion_marches.dto.MessageCreateDTO;
import com.stage.gestion_marches.dto.MessageResponseDTO;
import com.stage.gestion_marches.dto.ProblemeCreateDTO;
import com.stage.gestion_marches.dto.ProblemeResponseDTO;
import com.stage.gestion_marches.entity.EtatProbleme;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProblemeService {
    ProblemeResponseDTO declarerProbleme(ProblemeCreateDTO dto, Long utilisateurId, List<MultipartFile> fichiers);
    MessageResponseDTO ajouterMessage(Long problemeId, MessageCreateDTO dto, Long utilisateurId, List<MultipartFile> fichiers);
    ProblemeResponseDTO changerEtat(Long problemeId, EtatProbleme nouvelEtat);
    ProblemeResponseDTO getProblemeById(Long id);
    List<ProblemeResponseDTO> getProblemesByMarche(Long marcheId);
    List<ProblemeResponseDTO> getAllProblemes();
    public List<ProblemeResponseDTO> getProblemesByUtilisateur(Long utilisateurId);

}