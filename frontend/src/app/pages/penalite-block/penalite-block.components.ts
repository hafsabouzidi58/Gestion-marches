import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AvancementService, PenalitePrediction } from '../../services/avancement.service';
import { MarcheService, Marche } from '../../services/marche.service'; // 👈 Import du MarcheService

@Component({
  selector: 'app-penalite-block',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './penalite-block.component.html',
  styleUrls: ['./penalite-block.component.css']
})
export class PenaliteBlockComponent implements OnInit {
  @Input() marcheId?: number;
  prediction?: PenalitePrediction;
  marchesDisponibles: Marche[] = []; // 👈 Liste dynamique alimentée par la BDD

  chargementMarches: boolean = true;
  chargementPrediction: boolean = false;
  erreur: string | null = null;

  constructor(
    private avancementService: AvancementService,
    private marcheService: MarcheService, // 👈 Injection du MarcheService
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.chargerTousLesMarches();
  }

  chargerTousLesMarches(): void {
    this.chargementMarches = true;
    this.marcheService.getAll().subscribe({
      next: (marches) => {
        this.marchesDisponibles = marches;
        this.chargementMarches = false;

        if (this.marchesDisponibles.length > 0) {
          // 1. Vérifier si un ID est présent dans l'URL
          const routeId = this.route.snapshot.paramMap.get('id');

          if (routeId) {
            this.marcheId = Number(routeId);
          } else if (!this.marcheId) {
            // 2. Sinon, sélectionner le premier marché de la BDD par défaut
            this.marcheId = this.marchesDisponibles[0].id;
          }

          this.chargerPrediction();
        }
      },
      error: (err) => {
        console.error('Erreur chargement des marchés:', err);
        this.erreur = 'Impossible de charger la liste des marchés depuis la base de données.';
        this.chargementMarches = false;
      }
    });
  }

  onMarcheChange(newId: number): void {
    this.marcheId = Number(newId);
    this.chargerPrediction();
  }

  chargerPrediction(): void {
    if (!this.marcheId) return;

    this.chargementPrediction = true;
    this.erreur = null;

    this.avancementService.getPredictionPenalites(this.marcheId).subscribe({
      next: (data) => {
        this.prediction = data;
        this.chargementPrediction = false;
      },
      error: (err) => {
        console.error('Erreur calcul pénalités:', err);
        this.erreur = `Impossible de calculer la prédiction pour le marché sélectionné.`;
        this.chargementPrediction = false;
        this.prediction = undefined;
      }
    });
  }

  getPourcentagePlafond(): number {
    if (!this.prediction?.montantPlafondMax) return 0;
    const pct = (this.prediction.montantPenaliteEstime / this.prediction.montantPlafondMax) * 100;
    return Math.min(100, Math.round(pct));
  }
}
