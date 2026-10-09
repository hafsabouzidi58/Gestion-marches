package com.stage.gestion_marches.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "avancements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avancement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marche_id")
    private Marche marche;

    @Column(name = "taux_prevu", precision = 5, scale = 2)
    private BigDecimal tauxPrevu;

    @Column(name = "taux_reel", precision = 5, scale = 2)
    private BigDecimal tauxReel;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_avancement")
    @Builder.Default
    private LocalDate dateAvancement = LocalDate.now();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "avancement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PieceJointeAvancement> piecesJointes = new ArrayList<>();
}
