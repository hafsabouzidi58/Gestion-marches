import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule ,FormsModule} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ProblemeService, Marche } from '../../services/probleme.service';
import { Priorite, ProblemeCreateDTO, ProblemeResponseDTO, PieceJointeResponseDTO ,MessageCreateDTO} from '../../models/probleme.model';

@Component({
  selector: 'app-declarer-probleme',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule,FormsModule],
  templateUrl: './declarer-probleme.component.html',
  styleUrls: ['./declarer-probleme.component.css']
})
export class DeclarerProblemeComponent implements OnInit {

  problemeForm!: FormGroup;
  priorites = Object.values(Priorite);
  marches: Marche[] = [];
  fichiersSelectionnes: File[] = [];

  utilisateurConnecteId: number = 7; // ID du responsable connecté
  messageSucces: string = '';
  messageErreur: string = '';
  enCoursEnvoi: boolean = false;

  // 💡 Nouveaux champs pour le tableau de suivi des déclarations
  mesProblemes: ProblemeResponseDTO[] = [];
  problemeChat: ProblemeResponseDTO | null = null;
  nouveauMessageTexte: string = '';
  fichiersMessage: File[] = [];
  enCoursEnvoiMessage: boolean = false;
  constructor(
    private fb: FormBuilder,
    private problemeService: ProblemeService
  ) {}

  ngOnInit(): void {
    // Récupération automatique de l'ID 7 depuis l'objet 'user' du localStorage
    const userJson = localStorage.getItem('user');
    if (userJson) {
      const user = JSON.parse(userJson);
      this.utilisateurConnecteId = user.id; // Va automatiquement valoir 7 !
    }

    this.problemeForm = this.fb.group({
      marcheId: ['', Validators.required],
      titre: ['', [Validators.required, Validators.minLength(5)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      priorite: [Priorite.HAUTE, Validators.required]
    });

    this.chargerMarches();
    this.chargerMesProblemes();
  }

  chargerMarches(): void {
    this.problemeService.getMarches().subscribe({
      next: (data) => this.marches = data,
      error: (err) => console.error('Erreur chargement marchés', err)
    });
  }

  // 💡 Méthode pour récupérer les problèmes déclarés par l'utilisateur connecté
  chargerMesProblemes(): void {
    console.log('ID utilisateur envoyé au backend :', this.utilisateurConnecteId);

    if (!this.utilisateurConnecteId) {
      console.error('⚠️ Aucun ID utilisateur trouvé dans le LocalStorage !');
      return;
    }

    this.problemeService.getProblemesByUtilisateur(this.utilisateurConnecteId).subscribe({
      next: (data) => {
        console.log('Problèmes reçus du backend :', data); // 👈 Regardez ce tableau dans F12
        this.mesProblemes = data;
      },
      error: (err) => console.error('Erreur chargement de mes déclarations', err)
    });
  }

  // 💡 Méthode pour télécharger une pièce jointe de la liste
  telechargerFichier(pj: PieceJointeResponseDTO): void {
    const nom = pj.nomFichier || pj.nomOriginal || 'fichier';
    this.problemeService.downloadFichier(pj.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = nom;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => console.error('Erreur de téléchargement', err)
    });
  }

  onFichiersSelect(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      this.fichiersSelectionnes = Array.from(event.target.files);
    }
  }

  retirerFichier(index: number): void {
    this.fichiersSelectionnes.splice(index, 1);
  }

  onSubmit(): void {
    if (this.problemeForm.invalid) {
      return;
    }

    this.enCoursEnvoi = true;
    this.messageSucces = '';
    this.messageErreur = '';

    const dto: ProblemeCreateDTO = {
      marcheId: Number(this.problemeForm.value.marcheId),
      titre: this.problemeForm.value.titre,
      description: this.problemeForm.value.description,
      priorite: this.problemeForm.value.priorite
    };

    this.problemeService.declarerProbleme(dto, this.utilisateurConnecteId, this.fichiersSelectionnes)
      .subscribe({
        next: (response) => {
          this.messageSucces = `Problème N°${response.id} déclaré avec succès !`;
          this.problemeForm.reset({ priorite: Priorite.HAUTE, marcheId: '' });
          this.fichiersSelectionnes = [];
          this.enCoursEnvoi = false;
          this.chargerMesProblemes(); // 👈 Rafraîchit automatiquement le tableau sous le formulaire
        },
        error: (err) => {
          console.error(err);
          this.messageErreur = 'Erreur lors de la déclaration du problème.';
          this.enCoursEnvoi = false;
        }
      });
  }
  ouvrirChat(probleme: ProblemeResponseDTO): void {
    this.problemeChat = probleme;
    this.nouveauMessageTexte = '';
    this.fichiersMessage = [];
  }

  fermerChat(): void {
    this.problemeChat = null;
  }

  onFichiersMessageSelect(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      this.fichiersMessage = Array.from(event.target.files);
    }
  }

  envoyerMessage(): void {
    if (!this.nouveauMessageTexte.trim() || !this.problemeChat) return;

    this.enCoursEnvoiMessage = true;
    const dto: MessageCreateDTO = { contenu: this.nouveauMessageTexte };

    this.problemeService.ajouterMessage(
      this.problemeChat.id,
      dto,
      this.utilisateurConnecteId,
      this.fichiersMessage
    ).subscribe({
      next: (msgResponse) => {
        if (!this.problemeChat!.messages) {
          this.problemeChat!.messages = [];
        }
        this.problemeChat!.messages.push(msgResponse);

        // Réinitialisation du champ texte et des fichiers
        this.nouveauMessageTexte = '';
        this.fichiersMessage = [];

        // Réinitialiser la valeur de l'input type="file" dans le DOM
        const fileInput = document.getElementById('fichiersMessage') as HTMLInputElement;
        if (fileInput) fileInput.value = '';

        this.enCoursEnvoiMessage = false;
      },
      error: (err) => {
        console.error('Erreur lors de l\'envoi du message', err);
        this.enCoursEnvoiMessage = false;
      }
    });
  }
}
