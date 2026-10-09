package com.stage.gestion_marches.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "execution_delais")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionDelai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marche_id", unique = true)
    private Marche marche;

    @Column(name = "date_notification")
    private LocalDate dateNotification;

    @Column(name = "date_reception_notification")
    private LocalDate dateReceptionNotification;

    @Column(name = "date_ordre_service")
    private LocalDate dateOrdreService;

    @Column(name = "delai_execution_mois")
    private Integer delaiExecutionMois;

    @Column(name = "date_achevement_preveu")
    private LocalDate dateAchevementPreveu;
}