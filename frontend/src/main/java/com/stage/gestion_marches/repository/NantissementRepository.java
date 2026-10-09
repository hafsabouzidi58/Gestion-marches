package com.stage.gestion_marches.repository;

import com.stage.gestion_marches.entity.Nantissement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NantissementRepository extends JpaRepository<Nantissement, Long> {

    Optional<Nantissement> findByMarcheId(Long marcheId);

    void deleteByMarcheId(Long marcheId);

    // Recherche par mot-clé (numéro de marché, banque, compte bancaire)
    @Query("SELECT n FROM Nantissement n WHERE " +
            "LOWER(n.marche.numMarche) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(n.banque) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(n.compteBancaire) LIKE LOWER(CONCAT('%', :kw, '%'))")
    List<Nantissement> searchNantissements(@Param("kw") String keyword);
}