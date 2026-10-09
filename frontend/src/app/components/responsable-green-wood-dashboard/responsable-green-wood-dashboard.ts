import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AvancementService, PenalitePrediction } from '../../services/avancement.service';

@Component({
  selector: 'app-responsable-green-wood-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './responsable-green-wood-dashboard.html',
  styleUrl: './responsable-green-wood-dashboard.css',
})
export class ResponsableGreenWoodDashboard implements OnInit {
  alertesPenalites: PenalitePrediction[] = [];
  chargementAlertes: boolean = true;
  erreurAlertes: string | null = null;

  constructor(private avancementService: AvancementService) {}

  ngOnInit(): void {
    this.chargerAlertesPenalites();
  }

  chargerAlertesPenalites(): void {
    this.chargementAlertes = true;
    this.avancementService.getToutesLesAlertesPenalites().subscribe({
      next: (alertes: PenalitePrediction[]) => { // 👈 Typage explicite
        this.alertesPenalites = alertes;
        this.chargementAlertes = false;
      },
      error: (err: unknown) => { // 👈 Typage explicite
        console.error('Erreur chargement des alertes:', err);
        this.erreurAlertes = 'Impossible de charger les alertes de pénalités.';
        this.chargementAlertes = false;
      }
    });
  }

  getPourcentagePlafond(prediction: PenalitePrediction): number {
    if (!prediction.montantPlafondMax) return 0;
    const pct = (prediction.montantPenaliteEstime / prediction.montantPlafondMax) * 100;
    return Math.min(100, Math.round(pct));
  }
}
