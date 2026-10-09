package com.stage.gestion_marches.repository;

import com.stage.gestion_marches.entity.EtatProbleme;
import com.stage.gestion_marches.entity.ProblemeMarche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemeMarcheRepository extends JpaRepository<ProblemeMarche, Long> {
    List<ProblemeMarche> findByMarcheIdOrderByDateDeclarationDesc(Long marcheId);
    List<ProblemeMarche> findByDeclareParIdOrderByDateDeclarationDesc(Long utilisateurId);
    List<ProblemeMarche> findByEtat(EtatProbleme etat);

}