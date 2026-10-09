package com.stage.gestion_marches.repository;

import com.stage.gestion_marches.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByMarcheId(Long marcheId);
}