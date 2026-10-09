package com.stage.gestion_marches.repository;

import com.stage.gestion_marches.entity.Prestataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestataireRepository extends JpaRepository<Prestataire, Long> {

    // 🟢 Récupère tous les prestataires triés par ID (évite le saut de ligne en SQL)
    List<Prestataire> findAllByOrderByIdAsc();

    // 🟢 Récupère les prestataires actifs triés par ID
    List<Prestataire> findByActifTrueOrderByIdAsc();
}