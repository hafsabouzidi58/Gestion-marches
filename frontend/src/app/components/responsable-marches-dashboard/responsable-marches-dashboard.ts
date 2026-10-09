import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Chart, registerables } from 'chart.js';
import { firstValueFrom } from 'rxjs';

// Chemins d'accès corrigés vers vos services
import { MarcheService, Marche } from '../../services/marche.service';
import { DecompteService } from '../../services/decompte.service';

Chart.register(...registerables);

@Component({
  selector: 'app-responsable-marches-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './responsable-marches-dashboard.html',
  styleUrl: './responsable-marches-dashboard.css',
})
export class ResponsableMarchesDashboard implements OnInit, AfterViewInit {
  marches: Marche[] = [];
  alertesCount: number = 0;
  totalBudget: number = 0;
  totalConsomme: number = 0;
  soldeGlobal: number = 0;
  tauxConsommation: number = 0;
  activeMarchesCount: number = 0;

  isLoading: boolean = true;
  chartStatus: Chart | null = null;
  chartBudget: Chart | null = null;

  constructor(
    private marcheService: MarcheService,
    private decompteService: DecompteService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  ngAfterViewInit(): void {
    // Les graphiques s'initialisent après le chargement des données dans loadDashboardData()
  }

  loadDashboardData(): void {
    this.isLoading = true;

    // Typage explicite du paramètre 'data' et 'err'
    this.marcheService.getAll().subscribe({
      next: (data: Marche[]) => {
        this.marches = data;
        this.calculateMetrics();
        this.isLoading = false;
        setTimeout(() => this.initCharts(), 100);
      },
      error: (err: unknown) => {
        console.error('Erreur lors du chargement des données dashboard', err);
        this.isLoading = false;
      }
    });

    // Typage explicite des alertes
    this.marcheService.getAlertesEcheance(30).subscribe({
      next: (alertes: Marche[]) => {
        this.alertesCount = alertes.length;
      },
      error: (err: unknown) => console.error('Erreur alertes', err)
    });
  }

 calculateMetrics(): void {
   this.activeMarchesCount = this.marches.filter((m: Marche) => m.actif).length;

   // Conversion explicite avec Number() pour garantir un calcul purement numérique
   this.totalBudget = this.marches.reduce(
     (acc: number, m: Marche) => acc + Number(m.budget || 0),
     0
   );

   // Correction du typage du tableau de promesses
   const promises: Promise<number>[] = this.marches.map(async (m: Marche) => {
     if (m.id) {
       const res = await firstValueFrom(this.decompteService.getSoldeRestant(m.id));
       return Number(res || 0);
     }
     return Number(m.budget || 0);
   });

   Promise.all(promises).then((soldes: number[]) => {
     const totalRestant = soldes.reduce(
       (acc: number, val: number) => acc + Number(val || 0),
       0
     );
     this.soldeGlobal = totalRestant;
     this.totalConsomme = this.totalBudget - this.soldeGlobal;
     this.tauxConsommation =
       this.totalBudget > 0 ? (this.totalConsomme / this.totalBudget) * 100 : 0;
   });
 }


  initCharts(): void {
    // 1. Graphique Statut des marchés (Donut)
    const ctxStatus = document.getElementById('statusChart') as HTMLCanvasElement;
    if (ctxStatus) {
      if (this.chartStatus) this.chartStatus.destroy();
      this.chartStatus = new Chart(ctxStatus, {
        type: 'doughnut',
        data: {
          labels: ['Marchés Actifs', 'Marchés Inactifs'],
          datasets: [{
            data: [this.activeMarchesCount, this.marches.length - this.activeMarchesCount],
            backgroundColor: ['#10b981', '#6b7280'],
            borderWidth: 0
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { position: 'bottom' }
          }
        }
      });
    }

    // 2. Graphique Consommation Budgétaire (Bar Chart)
    const ctxBudget = document.getElementById('budgetChart') as HTMLCanvasElement;
    if (ctxBudget) {
      if (this.chartBudget) this.chartBudget.destroy();
      this.chartBudget = new Chart(ctxBudget, {
        type: 'bar',
        data: {
          labels: ['Budget Total', 'Montant Consommé', 'Solde Restant'],
          datasets: [{
            label: 'Montant (DH)',
            data: [this.totalBudget, this.totalConsomme, this.soldeGlobal],
            backgroundColor: ['#3b82f6', '#f59e0b', '#10b981'],
            borderRadius: 6
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { display: false }
          },
          scales: {
            y: { beginAtZero: true }
          }
        }
      });
    }
  }
}
