package com.stage.gestion_marches.repository;

import com.stage.gestion_marches.entity.Marche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MarcheRepository extends JpaRepository<Marche, Long> {

    // Récupérer uniquement les marchés actifs
    List<Marche> findByActifTrue();

    // Recherche directe par exercice
    List<Marche> findByExercice(String exercice);

    // Recherche globale (numéro de marché, objet ou raison sociale du prestataire) sans utiliser de requête explicite
    List<Marche> findByNumMarcheContainingIgnoreCaseOrObjetMarcheContainingIgnoreCaseOrPrestataireNomSocieteContainingIgnoreCase(
            String numMarche,
            String objetMarche,
            String nomSociete
    );

    // Option Intelligente : Détecter les marchés actifs arrivant à échéance entre aujourd'hui et la date limite
    List<Marche> findByActifTrueAndDateFinPrevueBetween(LocalDate startDate, LocalDate endDate);

    List<Marche> findAllByOrderByIdAsc();

    // Ou si vous souhaitez ne récupérer que les marchés actifs par défaut
    List<Marche> findByActifTrueOrderByIdAsc();


}