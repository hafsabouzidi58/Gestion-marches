package com.stage.gestion_marches.repository;

import com.stage.gestion_marches.entity.Avancement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvancementRepository extends JpaRepository<Avancement, Long> {

    // Historique complet par marché
    List<Avancement> findByMarcheIdOrderByDateAvancementDesc(Long marcheId);

    // Récupérer le tout dernier avancement saisi sur un marché
    Optional<Avancement> findFirstByMarcheIdOrderByDateAvancementDescCreatedAtDesc(Long marcheId);

    // Recherche d'avancements par mot-clé dans la description (avec tri descendant)
    List<Avancement> findByMarche_IdAndDescriptionContainingIgnoreCaseOrderByDateAvancementDesc(Long marcheId, String keyword);

    // Recherche par plage de dates
    List<Avancement> findByMarcheIdAndDateAvancementBetweenOrderByDateAvancementDesc(Long marcheId, LocalDate startDate, LocalDate endDate);
}