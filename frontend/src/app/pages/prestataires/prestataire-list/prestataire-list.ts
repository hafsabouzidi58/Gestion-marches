import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Prestataire, PrestataireService } from '../../../services/prestataire';

@Component({
  selector: 'app-prestataire-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './prestataire-list.html',
  styleUrls: ['./prestataire-list.css'],
  encapsulation: ViewEncapsulation.None
})
export class PrestataireListComponent implements OnInit {
  prestataires: Prestataire[] = [];
  filteredPrestataires: Prestataire[] = [];
  searchTerm: string = '';

  constructor(private prestataireService: PrestataireService) {}

  ngOnInit(): void {
    this.loadPrestataires();
  }

  loadPrestataires(): void {
    this.prestataireService.getAll().subscribe({
      next: (data: Prestataire[]) => {
        // 🟢 Tri systématique par ID pour garantir que l'ordre reste fixe
        const sortedData = data.sort((a, b) => (a.id && b.id ? a.id - b.id : 0));
        this.prestataires = sortedData;
        this.onSearch(); // Réapplique le filtre de recherche en cours
      },
      error: (err: any) => console.error('Erreur chargement prestataires', err)
    });
  }

  onSearch(): void {
    if (!this.searchTerm.trim()) {
      this.filteredPrestataires = [...this.prestataires];
      return;
    }
    const term = this.searchTerm.toLowerCase().trim();
    this.filteredPrestataires = this.prestataires.filter(p =>
      p.nomSociete?.toLowerCase().includes(term) ||
      p.rc?.toLowerCase().includes(term) ||
      p.patente?.toLowerCase().includes(term) ||
      p.identifiantFiscal?.toLowerCase().includes(term)
    );
  }

  onToggleStatus(id: number): void {
    this.prestataireService.toggleStatus(id).subscribe({
      next: () => {
        // 🟢 Mise à jour LOCALE du statut pour éviter de recharger et déplacer la ligne
        const item = this.prestataires.find(p => p.id === id);
        if (item) {
          item.actif = !item.actif;
          this.onSearch(); // Met à jour l'affichage filtré sans réordonner les lignes
        }
      },
      error: (err: any) => console.error('Erreur lors de la modification du statut', err)
    });
  }

  onDelete(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer ce prestataire ?')) {
      this.prestataireService.delete(id).subscribe({
        next: () => this.loadPrestataires(),
        error: (err: any) => console.error('Erreur lors de la suppression', err)
      });
    }
  }
}
