import { Component, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService, UserDTO } from '../../services/user.service';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-profile.html',
  styleUrl: './edit-profile.css'
})
export class EditProfileComponent implements OnInit {
  user: UserDTO = {
    id: undefined,
    nom: '',
    prenom: '',
    email: '',
    motDePasseHash: '',
    role: '',
    actif: true
  };
  isLoading: boolean = false;

  constructor(
    private userService: UserService,
    private router: Router,
    private location: Location
  ) {}

  ngOnInit(): void {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const currentUser = JSON.parse(userStr);
      this.user = {
        id: currentUser.id,
        nom: currentUser.nom || '',
        prenom: currentUser.prenom || '',
        email: currentUser.email || '',
        motDePasseHash: '', // Laissé vide par défaut (saisi uniquement si changement souhaité)
        role: currentUser.role || '',
        actif: currentUser.actif ?? true
      };
    }
  }

  onSubmit(): void {
    if (this.user.id) {
      this.isLoading = true;

      // Préparation de l'objet à envoyer
      const updatePayload: Partial<UserDTO> = {
        id: this.user.id,
        nom: this.user.nom,
        prenom: this.user.prenom,
        email: this.user.email,
        // N'envoyer le champ mot de passe que s'il est non vide
        ...(this.user.motDePasseHash && this.user.motDePasseHash.trim() !== ''
            ? { motDePasseHash: this.user.motDePasseHash }
            : {})
      };

      this.userService.updateProfile(this.user.id, updatePayload).subscribe({
        next: (updatedUser) => {
          this.isLoading = false;
          alert('Profil mis à jour avec succès !');

          // Réinitialiser le champ mot de passe dans le formulaire
          this.user.motDePasseHash = '';

          // Mettre à jour la session dans le localStorage
          const stored = localStorage.getItem('user');
          if (stored) {
            const parsed = JSON.parse(stored);
            parsed.nom = updatedUser.nom;
            parsed.prenom = updatedUser.prenom;
            parsed.email = updatedUser.email;
            localStorage.setItem('user', JSON.stringify(parsed));
          }
        },
        error: (err) => {
          this.isLoading = false;
          console.error('Erreur mise à jour profil', err);
          alert('Échec de la mise à jour du profil.');
        }
      });
    }
  }

  onCancel(): void {
    this.location.back();
  }
}
