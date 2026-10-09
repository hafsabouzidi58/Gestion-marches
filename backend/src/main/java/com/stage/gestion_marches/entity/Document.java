package com.stage.gestion_marches.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "marche_id", nullable = false)
    private Long marcheId;

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Column(name = "type_document")
    private String typeDocument;

    @Column(name = "chemin_fichier", nullable = false)
    private String cheminFichier;

    @Column(name = "taille_ko")
    private Long tailleKo;

    @Column(name = "uploade_par")
    private Long uploadePar;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}