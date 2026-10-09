package com.stage.gestion_marches.repository;

import com.stage.gestion_marches.model.Decompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DecompteRepository extends JpaRepository<Decompte, Long> {

    List<Decompte> findByMarcheId(Long marcheId);

    // Calcule la somme des montants bruts consommés sur un marché
    @Query("SELECT COALESCE(SUM(d.montantBrut), 0) FROM Decompte d WHERE d.marche.id = :marcheId")
    BigDecimal sumMontantBrutByMarcheId(@Param("marcheId") Long marcheId);
}