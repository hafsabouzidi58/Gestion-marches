import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, RegisterRequest } from '../../../services/auth.service';
import { UserService, UserDTO } from '../../../services/user.service';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.html',
  styleUrls: ['./user-management.css']
})
export class UserManagement implements OnInit {

  users: UserDTO[] = [];

  newUser: RegisterRequest = {
    nom: '',
    prenom: '',
    email: '',
    motDePasse: '',
    role: 'RESPONSABLE_MARCHES'
  };

  loading = false;
  adding = false;

  successMessage = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  /**
   * Charger les utilisateurs depuis la base de données
   */
  loadUsers(): void {

    this.loading = true;
    this.errorMessage = '';

    this.userService.getAllUsers().subscribe({

      next: (users) => {
        this.users = users;
        this.loading = false;
      },

      error: (error) => {
        console.error(
          'Erreur chargement utilisateurs :',
          error
        );

        this.errorMessage =
          'Impossible de charger les utilisateurs.';

        this.loading = false;
      }

    });
  }

  /**
   * Ajouter un utilisateur
   */
  addUser(): void {

    this.successMessage = '';
    this.errorMessage = '';

    if (
      !this.newUser.nom ||
      !this.newUser.prenom ||
      !this.newUser.email ||
      !this.newUser.motDePasse ||
      !this.newUser.role
    ) {

      this.errorMessage =
        'Veuillez remplir tous les champs.';

      return;
    }

    this.adding = true;

    this.authService.register(this.newUser).subscribe({

      next: (response) => {

        console.log(
          'Utilisateur créé :',
          response
        );

        this.successMessage =
          'Utilisateur ajouté avec succès.';

        // Réinitialiser le formulaire
        this.newUser = {
          nom: '',
          prenom: '',
          email: '',
          motDePasse: '',
          role: 'RESPONSABLE_MARCHES'
        };

        this.adding = false;

        // Recharger la liste depuis la BDD
        this.loadUsers();
      },

      error: (error) => {

        console.error(
          'Erreur lors de la création :',
          error
        );

        if (error.error) {
          this.errorMessage =
            typeof error.error === 'string'
              ? error.error
              : 'Erreur lors de la création de l’utilisateur.';
        } else {
          this.errorMessage =
            'Erreur lors de la création de l’utilisateur.';
        }

        this.adding = false;
      }

    });
  }

  /**
   * Activer / désactiver un utilisateur
   */
  toggleUserStatus(user: UserDTO): void {

    if (!user.id) {
      return;
    }

    const action = user.actif
      ? 'désactiver'
      : 'activer';

    const confirmed = confirm(
      `Voulez-vous vraiment ${action} ${user.nom} ${user.prenom} ?`
    );

    if (!confirmed) {
      return;
    }

    this.successMessage = '';
    this.errorMessage = '';

    this.userService.toggleUserStatus(user.id).subscribe({

      next: (updatedUser) => {

        const index = this.users.findIndex(
          u => u.id === updatedUser.id
        );

        if (index !== -1) {
          this.users[index] = updatedUser;
        }

        this.successMessage = updatedUser.actif
          ? 'Utilisateur activé avec succès.'
          : 'Utilisateur désactivé avec succès.';
      },

      error: (error) => {

        console.error(
          'Erreur changement statut :',
          error
        );

        this.errorMessage =
          'Impossible de modifier le statut de l’utilisateur.';
      }

    });
  }
}
