import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DecompteService } from '../../services/decompte.service';

@Component({
  selector: 'app-saisie-decompte',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './saisie-decompte.component.html',
  styleUrls: ['./saisie-decompte.component.css']
})
export class SaisieDecompteComponent implements OnInit {
  @Input() marcheId!: number; // L'ID du marché sélectionné

  decompteForm!: FormGroup;
  isSubmitting = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private decompteService: DecompteService
  ) {}

  ngOnInit(): void {
    // Obtenir la date du jour au format YYYY-MM-DD
    const today = new Date().toISOString().split('T')[0];

    this.decompteForm = this.fb.group({
      marcheId: [this.marcheId, Validators.required],
      // Renommé en 'numDecompte' (String) pour correspondre au DTO Spring Boot
      numDecompte: ['1', [Validators.required]],
      // Ajout de 'dateDecompte' requis par le backend
      dateDecompte: [today, [Validators.required]],
      montantBrut: ['', [Validators.required, Validators.min(0.01)]]
    });
  }

  onSubmit(): void {
    if (this.decompteForm.invalid) {
      this.decompteForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = null;
    this.successMessage = null;

    // S'assurer que numDecompte est transmis sous forme de chaîne de caractères (String)
    const payload = {
      ...this.decompteForm.value,
      numDecompte: String(this.decompteForm.value.numDecompte)
    };

    this.decompteService.enregistrerDecompte(payload).subscribe({
      next: () => {
        this.successMessage = 'Décompte enregistré avec succès !';
        this.isSubmitting = false;
        this.decompteForm.reset();
      },
      error: (err: any) => {
        // Affiche la liste des erreurs renvoyées par Spring Boot
        if (err.error && typeof err.error === 'object') {
          this.errorMessage = err.error.message || JSON.stringify(err.error);
        } else {
          this.errorMessage = 'Erreur lors de l\'enregistrement du décompte.';
        }
        this.isSubmitting = false;
      }
    });
  }
}
