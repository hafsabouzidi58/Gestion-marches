package com.stage.gestion_marches.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pieces_jointes_avancement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceJointeAvancement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Column(name = "chemin_fichier", nullable = false)
    private String cheminFichier;

    @Column(name = "type_fichier")
    private String typeFichier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avancement_id")
    @JsonIgnore
    private Avancement avancement;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}