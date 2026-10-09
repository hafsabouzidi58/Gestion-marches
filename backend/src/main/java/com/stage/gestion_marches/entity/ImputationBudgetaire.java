package com.stage.gestion_marches.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "imputations_budgetaires")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImputationBudgetaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer exercice;

    @Column(name = "type_budget")
    private String typeBudget;

    private String article;
    private String paragraphe;
    private String ligne;
    private String rubrique;
}