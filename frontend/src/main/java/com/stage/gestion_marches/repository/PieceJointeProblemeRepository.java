package com.stage.gestion_marches.repository;
import com.stage.gestion_marches.entity.PieceJointeProbleme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PieceJointeProblemeRepository extends JpaRepository<PieceJointeProbleme, Long> {
}