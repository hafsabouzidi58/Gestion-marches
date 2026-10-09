package com.stage.gestion_marches.repository;
import com.stage.gestion_marches.entity.MessageProbleme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageProblemeRepository extends JpaRepository<MessageProbleme, Long> {
    List<MessageProbleme> findByProblemeIdOrderByDateEnvoiAsc(Long problemeId);
}