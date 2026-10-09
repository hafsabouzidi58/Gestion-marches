import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MarcheService, Marche } from '../../../services/marche.service';
import { DocumentService, DocumentModel } from '../../../services/document.service';
import { DecompteService } from '../../../services/decompte.service';
import { Decompte } from '../../../models/decompte.model';

@Component({
  selector: 'app-marche-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './marche-list.html',
  styleUrls: ['./marche-list.css']
})
export class MarcheListComponent implements OnInit {
  marches: Marche[] = [];
  searchTerm: string = '';
  isAlerteMode: boolean = false;

  // Modals
  selectedMarcheDetails: Marche | null = null;
  selectedMarcheForDocs: Marche | null = null;
  selectedMarcheForDecompte: Marche | null = null;
  selectedMarcheForDecompteList: Marche | null = null;

  documents: DocumentModel[] = [];
  decomptes: Decompte[] = [];
  soldeRestantMap: { [marcheId: number]: number } = {};

  // Formulaire Décompte
  decompteForm!: FormGroup;
  isSubmittingDecompte: boolean = false;
  isLoadingDecomptes: boolean = false;
  decompteErrorMessage: string | null = null;
  decompteSuccessMessage: string | null = null;

  // Formulaire Document
  selectedFile: File | null = null;

  constructor(
    private marcheService: MarcheService,
    private documentService: DocumentService,
    private decompteService: DecompteService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.initDecompteForm();
    this.loadMarches();
  }

  private initDecompteForm(): void {
    const today = new Date().toISOString().split('T')[0];
    this.decompteForm = this.fb.group({
      marcheId: [null, [Validators.required]],
      numDecompte: ['1', [Validators.required]],
      dateDecompte: [today, [Validators.required]],
      montantBrut: ['', [Validators.required, Validators.min(0.01)]]
    });
  }

  loadMarches(): void {
    this.isAlerteMode = false;
    this.marcheService.getAll().subscribe({
      next: (data) => {
        this.marches = data;
        // Charger le solde pour chaque marché
        this.marches.forEach(m => {
          if (m.id) this.loadSoldeRestant(m.id);
        });
      },
      error: (err) => console.error('Erreur chargement marchés', err)
    });
  }

  loadSoldeRestant(marcheId: number): void {
    this.decompteService.getSoldeRestant(marcheId).subscribe({
      next: (solde) => {
        this.soldeRestantMap[marcheId] = solde;
      },
      error: (err) => console.error('Erreur chargement solde', err)
    });
  }

  onSearch(): void {
    if (!this.searchTerm.trim()) {
      this.loadMarches();
      return;
    }
    this.marcheService.search(this.searchTerm).subscribe({
      next: (data) => (this.marches = data),
      error: (err) => console.error('Erreur lors de la recherche', err)
    });
  }

  loadAlertes(): void {
    this.isAlerteMode = true;
    this.marcheService.getAlertesEcheance(30).subscribe({
      next: (data) => (this.marches = data),
      error: (err) => console.error('Erreur chargement des alertes', err)
    });
  }

  onToggleStatus(id: number): void {
    this.marcheService.toggleStatus(id).subscribe({
      next: () => {
        const marche = this.marches.find(m => m.id === id);
        if (marche) {
          marche.actif = !marche.actif;
        }
      },
      error: (err) => {
        console.error('Erreur lors du changement de statut :', err);
        alert('Impossible de modifier le statut du marché.');
      }
    });
  }

  // --- Modal Saisie Décompte ---
  openDecompteModal(marche: Marche): void {
    this.selectedMarcheForDecompte = marche;
    this.decompteErrorMessage = null;
    this.decompteSuccessMessage = null;
    const today = new Date().toISOString().split('T')[0];

    this.decompteForm.reset({
      marcheId: marche.id,
      numDecompte: '1',
      dateDecompte: today,
      montantBrut: ''
    });
  }

  closeDecompteModal(): void {
    this.selectedMarcheForDecompte = null;
    this.decompteForm.reset();
  }

  onSubmitDecompte(): void {
    if (this.decompteForm.invalid || !this.selectedMarcheForDecompte?.id) {
      this.decompteForm.markAllAsTouched();
      return;
    }

    this.isSubmittingDecompte = true;
    this.decompteErrorMessage = null;
    this.decompteSuccessMessage = null;

    const payload = {
      marcheId: this.selectedMarcheForDecompte.id,
      numDecompte: String(this.decompteForm.value.numDecompte),
      dateDecompte: this.decompteForm.value.dateDecompte,
      montantBrut: Number(this.decompteForm.value.montantBrut)
    };

    this.decompteService.enregistrerDecompte(payload).subscribe({
      next: () => {
        this.isSubmittingDecompte = false;
        this.decompteSuccessMessage = 'Décompte enregistré avec succès !';

        const mId = this.selectedMarcheForDecompte!.id!;
        this.loadSoldeRestant(mId);

        if (this.selectedMarcheForDecompteList?.id === mId) {
          this.loadDecomptesByMarche(mId);
        }

        setTimeout(() => {
          this.closeDecompteModal();
          this.loadMarches();
        }, 1200);
      },
      error: (err) => {
        this.isSubmittingDecompte = false;
        this.decompteErrorMessage = err.error?.message || 'Erreur lors de l\'enregistrement du décompte.';
        console.error('Erreur Backend :', err);
      }
    });
  }

  // --- Modal Liste des Décomptes ---
  openDecomptesListModal(marche: Marche): void {
    this.selectedMarcheForDecompteList = marche;
    if (marche.id) {
      this.loadDecomptesByMarche(marche.id);
      this.loadSoldeRestant(marche.id);
    }
  }

  closeDecomptesListModal(): void {
    this.selectedMarcheForDecompteList = null;
    this.decomptes = [];
  }

  loadDecomptesByMarche(marcheId: number): void {
    this.isLoadingDecomptes = true;
    this.decompteService.getByMarche(marcheId).subscribe({
      next: (data) => {
        this.decomptes = data;
        this.isLoadingDecomptes = false;
      },
      error: (err) => {
        console.error('Erreur chargement des décomptes', err);
        this.isLoadingDecomptes = false;
      }
    });
  }

  // --- Modal Détails ---
  openDetailsModal(marche: Marche): void {
    this.selectedMarcheDetails = marche;
    if (marche.id) {
      this.loadSoldeRestant(marche.id);
    }
  }

  closeDetailsModal(): void {
    this.selectedMarcheDetails = null;
  }

  // --- Modal Documents ---
  openDocsModal(marche: Marche): void {
    this.selectedMarcheForDocs = marche;
    if (marche.id) {
      this.loadDocuments(marche.id);
    }
  }

  closeDocsModal(): void {
    this.selectedMarcheForDocs = null;
    this.documents = [];
    this.selectedFile = null;
  }

  loadDocuments(marcheId: number): void {
    this.documentService.getByMarche(marcheId).subscribe({
      next: (docs) => (this.documents = docs),
      error: (err) => console.error('Erreur chargement documents', err)
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input && input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    } else {
      this.selectedFile = null;
    }
  }

  onUploadDoc(): void {
    if (!this.selectedMarcheForDocs?.id || !this.selectedFile) return;

    this.documentService
      .upload(this.selectedMarcheForDocs.id, this.selectedFile)
      .subscribe({
        next: () => {
          this.loadDocuments(this.selectedMarcheForDocs!.id!);
          this.selectedFile = null;
          const fileInput = document.getElementById('docFileInput') as HTMLInputElement;
          if (fileInput) fileInput.value = '';
        },
        error: (err) => {
          alert('Échec du téléversement : ' + (err.error?.message || err.message));
        }
      });
  }

  onDeleteDoc(id: number): void {
    if (!this.selectedMarcheForDocs?.id) return;

    if (confirm('Voulez-vous vraiment supprimer ce document ?')) {
      this.documentService.delete(id).subscribe({
        next: () => {
          this.loadDocuments(this.selectedMarcheForDocs!.id!);
        },
        error: (err) => console.error('Erreur suppression document', err)
      });
    }
  }

  onDownloadDoc(doc: DocumentModel): void {
    this.documentService.download(doc.id).subscribe({
      next: (blob: Blob) => {
        const fileBlob = new Blob([blob], { type: blob.type || 'application/octet-stream' });
        const downloadUrl = window.URL.createObjectURL(fileBlob);

        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = doc.nomFichier || 'document_telecharge';

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        window.URL.revokeObjectURL(downloadUrl);
      },
      error: (err) => console.error('Erreur lors du téléchargement du document', err)
    });
  }
}
