import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MarcheService, Marche } from '../../services/marche.service';

export interface CountdownInfo {
  days: number;
  hours: number;
  minutes: number;
  seconds: number;
  isOverdue: boolean;
  text: string;
}

@Component({
  selector: 'app-green-wood-marche-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './green-wood-marche-list.html',
  styleUrls: ['./green-wood-marche-list.css']
})
export class GreenWoodMarcheListComponent implements OnInit, OnDestroy {
  marches: Marche[] = [];
  searchTerm: string = '';
  isAlerteMode: boolean = false;

  selectedMarcheDetails: Marche | null = null;

  // Stockage du compte à rebours par ID de marché
  countdowns: { [marcheId: number]: CountdownInfo } = {};
  private timerId: any;

  constructor(private marcheService: MarcheService) {}

  ngOnInit(): void {
    this.loadMarches();
    // Rafraîchir le chrono chaque seconde
    this.timerId = setInterval(() => {
      this.updateAllCountdowns();
    }, 1000);
  }

  ngOnDestroy(): void {
    if (this.timerId) {
      clearInterval(this.timerId);
    }
  }

  loadMarches(): void {
    this.isAlerteMode = false;
    this.marcheService.getAll().subscribe({
      next: (data) => {
        console.log('Marchés reçus depuis le Backend:', data);
        // Sécurisation : s'assurer que data est bien un tableau
        this.marches = Array.isArray(data) ? data : [];
        this.updateAllCountdowns();
      },
      error: (err) => {
        console.error('Erreur HTTP lors du chargement des marchés :', err);
        alert('Impossible de charger les marchés. Vérifiez la console F12 (CORS, serveur backend non lancé, ou erreur 500).');
      }
    });
  }
  onSearch(): void {
    if (!this.searchTerm.trim()) {
      this.loadMarches();
      return;
    }
    this.marcheService.search(this.searchTerm).subscribe({
      next: (data) => {
        this.marches = data;
        this.updateAllCountdowns();
      },
      error: (err) => console.error('Erreur recherche', err)
    });
  }

  loadAlertes(): void {
    this.isAlerteMode = true;
    this.marcheService.getAlertesEcheance(30).subscribe({
      next: (data) => {
        this.marches = data;
        this.updateAllCountdowns();
      },
      error: (err) => console.error('Erreur alertes', err)
    });
  }

  resetFilters(): void {
    this.searchTerm = '';
    this.loadMarches();
  }

  openDetailsModal(marche: Marche): void {
    this.selectedMarcheDetails = marche;
  }

  closeDetailsModal(): void {
    this.selectedMarcheDetails = null;
  }

  // --- LOGIQUE CALCUL CHRONO TEMPS RÉEL ---
  private updateAllCountdowns(): void {
    const now = new Date().getTime();

    this.marches.forEach((m) => {
      if (m.id && m.dateFinPrevue) {
        const targetDate = new Date(m.dateFinPrevue).getTime();
        const diff = targetDate - now;

        const isOverdue = diff < 0;
        const absDiff = Math.abs(diff);

        const days = Math.floor(absDiff / (1000 * 60 * 60 * 24));
        const hours = Math.floor((absDiff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((absDiff % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((absDiff % (1000 * 60)) / 1000);

        let text = isOverdue
          ? `Dépassé de ${days}j ${hours}h ${minutes}m ${seconds}s`
          : `${days}j ${hours}h ${minutes}m ${seconds}s`;

        this.countdowns[m.id] = { days, hours, minutes, seconds, isOverdue, text };
      }
    });
  }

  getCountdown(id: number): CountdownInfo | null {
    return this.countdowns[id] || null;
  }
}
