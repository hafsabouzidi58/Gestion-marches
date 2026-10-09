package com.stage.gestion_marches.entity;
import com.stage.gestion_marches.entity.TypeGarantie;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "garanties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Garantie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marche_id")
    private Marche marche;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_garantie", nullable = false)
    private TypeGarantie typeGarantie;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_constitution")
    private LocalDate dateConstitution;

    @Column(name = "date_liberation")
    private LocalDate dateLiberation;

    @Column(name = "statut_liberation")
    private String statutLiberation;
}
