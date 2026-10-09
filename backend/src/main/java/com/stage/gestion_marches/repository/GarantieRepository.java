package com.stage.gestion_marches.repository;

import com.stage.gestion_marches.entity.Garantie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GarantieRepository extends JpaRepository<Garantie, Long> {

    // Récupérer toutes les garanties liées à un marché spécifique
    List<Garantie> findByMarcheId(Long marcheId);

    // Filtrer les garanties par statut (ex: EN_COURS ou LIBÉRÉE)
    List<Garantie> findByStatutLiberation(String statutLiberation);
}