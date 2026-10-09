import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MarcheService, Marche } from '../../services/marche.service'; // Ajustez selon votre arborescence

export interface Nantissement {
  id?: number;
  marcheId: number;
  compteBancaire: string;
  banque: string;
  montantInitial: number;
  interetsMoratoires: number;
  sommeAValoire: number;
  totalEngage: number;
  cp: number;
  ce: number;
  montantCautionDefinitive: number;
  dateConstitutionCaution?: string;
  dateLiberationCaution?: string;
  montantRetenueGarantie: number;
  dateConstitutionRetenue?: string;
  dateLiberationRetenue?: string;
  marche?: Marche;
}

@Component({
  selector: 'app-nantissement-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './nantissement-list.component.html',
  styleUrls: ['./nantissement-list.component.css']
})
export class NantissementListComponent implements OnInit {
  marches: Marche[] = [];
  nantissements: Nantissement[] = [];
  filteredNantissements: Nantissement[] = [];

  searchTerm: string = '';
  isModalOpen: boolean = false;
  isEditMode: boolean = false;

  currentNantissement: Nantissement = this.initNantissement();

  constructor(private marcheService: MarcheService) {}

  ngOnInit(): void {
    this.loadMarches();
  }

  private initNantissement(): Nantissement {
    return {
      marcheId: 0,
      compteBancaire: '',
      banque: '',
      montantInitial: 0,
      interetsMoratoires: 0,
      sommeAValoire: 0,
      totalEngage: 0,
      cp: 0,
      ce: 0,
      montantCautionDefinitive: 0,
      dateConstitutionCaution: '',
      dateLiberationCaution: '',
      montantRetenueGarantie: 0,
      dateConstitutionRetenue: '',
      dateLiberationRetenue: ''
    };
  }

  // 1. Charger les marchés
  loadMarches(): void {
    this.marcheService.getAll().subscribe({
      next: (data: Marche[]) => {
        this.marches = data;
        this.loadNantissements();
      },
      error: (err: any) => console.error('Erreur chargement marchés:', err)
    });
  }

  // 2. Charger les nantissements (Exemple basé exactement sur votre enregistrement SQL)
  loadNantissements(): void {
    this.nantissements = [
      {
        id: 4,
        marcheId: 2,
        compteBancaire: '007 810 000 594 100 000 040 923',
        banque: 'Banque Populaireee',
        montantInitial: 0.00,
        interetsMoratoires: 0.00,
        sommeAValoire: 0.00,
        totalEngage: 0.00,
        cp: 0.00,
        ce: 0.00,
        montantCautionDefinitive: 0.00,
        dateConstitutionCaution: '2024-04-01',
        dateLiberationCaution: '',
        montantRetenueGarantie: 0.00,
        dateConstitutionRetenue: 'Sur DP',
        dateLiberationRetenue: ''
      }
    ];
    this.filteredNantissements = [...this.nantissements];
  }

  // 3. Récupérer le vrai numéro de marché (m.numMarche) via l'ID
  getNumeroMarche(item: Nantissement): string {
    if (item.marche && item.marche.numMarche) {
      return item.marche.numMarche;
    }
    const marcheTrouve = this.marches.find(m => m.id === item.marcheId);
    return marcheTrouve ? marcheTrouve.numMarche : `N° ${item.marcheId}`;
  }

  // 4. Modales
  openCreateModal(): void {
    this.isEditMode = false;
    this.currentNantissement = this.initNantissement();
    if (this.marches.length > 0) {
      this.currentNantissement.marcheId = this.marches[0].id || 0;
    }
    this.isModalOpen = true;
  }

  openEditModal(item: Nantissement): void {
    this.isEditMode = true;
    this.currentNantissement = { ...item };
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  saveNantissement(): void {
    if (this.isEditMode) {
      const index = this.nantissements.findIndex(i => i.id === this.currentNantissement.id);
      if (index !== -1) {
        this.nantissements[index] = { ...this.currentNantissement };
      }
    } else {
      const newId = this.nantissements.length > 0 ? Math.max(...this.nantissements.map(n => n.id || 0)) + 1 : 1;
      this.nantissements.push({ ...this.currentNantissement, id: newId });
    }

    this.onSearch();
    this.closeModal();
  }

  deleteNantissement(id?: number): void {
    if (confirm('Voulez-vous vraiment supprimer ce nantissement ?')) {
      this.nantissements = this.nantissements.filter(item => item.id !== id);
      this.onSearch();
    }
  }

  // 5. Recherche multicritère
  onSearch(): void {
    if (!this.searchTerm.trim()) {
      this.filteredNantissements = [...this.nantissements];
      return;
    }

    const term = this.searchTerm.toLowerCase().trim();
    this.filteredNantissements = this.nantissements.filter(item => {
      const numMarche = this.getNumeroMarche(item).toLowerCase();
      const banque = (item.banque || '').toLowerCase();
      const compte = (item.compteBancaire || '').toLowerCase();

      return numMarche.includes(term) || banque.includes(term) || compte.includes(term);
    });
  }
}
