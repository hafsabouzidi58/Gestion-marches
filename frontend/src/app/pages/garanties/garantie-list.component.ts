import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GarantieService } from '../../services/garantie.service';
import { Garantie } from '../../models/garantie.model';

@Component({
  selector: 'app-garantie-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './garantie-list.component.html',
  styleUrls: ['./garantie-list.component.css']
})
export class GarantieListComponent implements OnInit {
  garanties: Garantie[] = [];
  selectedGarantie: Partial<Garantie> = {};
  isEditMode: boolean = false;
  showModal: boolean = false;

  typesGarantie: string[] = [
    'CAUTION_PROVISOIRE',
    'CAUTION_DEFINITIVE',
    'RETENUE_DE_GARANTIE',
    'CAUTION_AVANCE'
  ];

  constructor(private garantieService: GarantieService) {}

  ngOnInit(): void {
    this.loadGaranties();
  }

  loadGaranties(): void {
    this.garantieService.getAllGaranties().subscribe({
      next: (data) => (this.garanties = data),
      error: (err: any) => console.error('Erreur chargement:', err)
    });
  }

  countByStatut(statut: string): number {
    if (!this.garanties) return 0;
    return this.garanties.filter(g => g.statutLiberation === statut).length;
  }

  formatTypeGarantie(type?: string): string {
    if (!type) return '';
    switch (type) {
      case 'CAUTION_PROVISOIRE': return 'Caution Provisoire';
      case 'CAUTION_DEFINITIVE': return 'Caution Définitive';
      case 'RETENUE_DE_GARANTIE': return 'Retenue de Garantie';
      case 'CAUTION_AVANCE': return 'Caution d\'Avance';
      default: return type;
    }
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.selectedGarantie = {
      typeGarantie: this.typesGarantie[0] // Valeur par défaut
    };
    this.showModal = true;
  }

  openEditModal(garantie: Garantie): void {
    this.isEditMode = true;
    // Formatage explicite de la date pour l'input type="date"
    let formattedDate = garantie.dateConstitution;
    if (formattedDate && formattedDate.includes('T')) {
      formattedDate = formattedDate.split('T')[0];
    }

    this.selectedGarantie = {
      ...garantie,
      dateConstitution: formattedDate
    };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedGarantie = {};
  }
saveGarantie(): void {
  const payload = {
    id: this.selectedGarantie.id,
    marcheId: Number(this.selectedGarantie.marcheId),
    typeGarantie: this.selectedGarantie.typeGarantie,
    montant: Number(this.selectedGarantie.montant),
    dateConstitution: this.selectedGarantie.dateConstitution,
    statutLiberation: this.selectedGarantie.statutLiberation || 'EN_COURS'
  };

  if (this.isEditMode && this.selectedGarantie.id) {
    this.garantieService.updateGarantie(this.selectedGarantie.id, payload as any).subscribe({
      next: () => {
        this.loadGaranties();
        this.closeModal();
      },
      error: (err: any) => console.error('Erreur lors de la modification:', err)
    });
  } else {
    this.garantieService.createGarantie(payload as any).subscribe({
      next: () => {
        this.loadGaranties();
        this.closeModal();
      },
      error: (err: any) => console.error('Erreur lors de la création:', err)
    });
  }
}
  libererGarantie(id: number): void {
    if (confirm('Voulez-vous vraiment libérer cette garantie ?')) {
      this.garantieService.libererGarantie(id).subscribe({
        next: () => this.loadGaranties(),
        error: (err: any) => console.error('Erreur lors de la libération:', err)
      });
    }
  }

  deleteGarantie(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer cette garantie ?')) {
      this.garantieService.deleteGarantie(id).subscribe({
        next: () => this.loadGaranties(),
        error: (err: any) => console.error('Erreur lors de la suppression:', err)
      });
    }
  }
}
