import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { AdminDashboard } from './components/admin-dashboard/admin-dashboard';
import { ResponsableMarchesDashboard } from './components/responsable-marches-dashboard/responsable-marches-dashboard';
import { ResponsableGreenWoodDashboard } from './components/responsable-green-wood-dashboard/responsable-green-wood-dashboard';
import { roleGuard } from './guards/role-guard';
import { MarcheListComponent } from './pages/marches/marche-list/marche-list';
import { MarcheFormComponent } from './pages/marches/marche-form/marche-form';
import { GreenWoodLayoutComponent } from './components/responsable-green-wood-dashboard/responsable-green-wood-layout/responsable-green-wood-layout';
import { GreenWoodMarcheListComponent } from './pages/green-wood-marche-list/green-wood-marche-list';
import { DeclarerProblemeComponent } from './components/declarer-probleme/declarer-probleme.component';
import { ListeProblemesComponent } from './pages/problemes/liste-problemes/liste-problemes.component';
import { AvancementComponent } from './pages/avancements/avancement.component';
import { PenaliteBlockComponent } from './pages/penalite-block/penalite-block.component';

// Layouts
import { EditProfileComponent } from './pages/edit-profile/edit-profile';
import { AdminLayout } from './components/admin-layout/admin-layout';
import { ResponsableLayoutComponent } from './components/responsable-marches-dashboard/responsable-marches-layout/responsable-marches-layout';
import { GreenWoodEditPrestataireComponent } from './pages/green-wood-edit-prestataire/green-wood-edit-prestataire';
// Composants Admin & Prestataires
import { UserManagement } from './components/admin-dashboard/user-management/user-management';
import { PrestataireListComponent } from './pages/prestataires/prestataire-list/prestataire-list';
import { PrestataireFormComponent } from './pages/prestataires/prestataire-form/prestataire-form';

// Component Nantissements
import { NantissementListComponent } from './pages/nantissements/nantissement-list.component';

// Component Garanties
import { GarantieListComponent } from './pages/garanties/garantie-list.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },

  // Redirection explicite si la connexion renvoie sur l'ancien path
  {
    path: 'RESPONSABLE_GREEN_WOOD/dashboard',
    redirectTo: 'green-wood/dashboard',
    pathMatch: 'full'
  },

  // Espace Green Wood unifié avec Layout
  {
    path: 'green-wood',
    component: GreenWoodLayoutComponent,
    canActivate: [roleGuard],
    data: { expectedRoles: ['RESPONSABLE_GREEN_WOOD', 'ADMIN_SYSTEME'] },
    children: [
      { path: 'dashboard', component: ResponsableGreenWoodDashboard },
      { path: 'marches', component: GreenWoodMarcheListComponent },
      { path: 'mon-compte', component: EditProfileComponent },
      { path: 'declarer-probleme', component: DeclarerProblemeComponent },
      { path: 'avancements', component: AvancementComponent },

      // Ou si votre route dépend de l'ID du marché :
      { path: 'marches/:id/avancements', component: AvancementComponent },
      // 👈 Route ajoutée
      // 🚀 AJOUTER CETTE ROUTE ICI
{ path: 'mes-infos', component: GreenWoodEditPrestataireComponent }, // ✏️ Édition des infos prestataire
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },
  // Espace Admin
  {
    path: 'admin',
    component: AdminLayout,
    canActivate: [roleGuard],
    data: { expectedRoles: ['ADMIN_SYSTEME'] },
    children: [
      { path: 'dashboard', component: AdminDashboard },
      { path: 'users', component: UserManagement },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },

  // Espace Responsable de Marchés
  {
    path: 'responsable-marches',
    component: ResponsableLayoutComponent,
    canActivate: [roleGuard],
    data: { expectedRoles: ['RESPONSABLE_MARCHES', 'ADMIN_SYSTEME'] },
    children: [
      { path: 'dashboard', component: ResponsableMarchesDashboard },
{ path: 'mon-compte', component: EditProfileComponent }, // 👈 Route ajoutée
      // Routes Prestataires
      { path: 'prestataires', component: PrestataireListComponent },
      { path: 'prestataires/nouveau', component: PrestataireFormComponent },
      { path: 'prestataires/edit/:id', component: PrestataireFormComponent },

      // Routes Marchés
      { path: 'marches', component: MarcheListComponent },
      { path: 'marches/nouveau', component: MarcheFormComponent },
      { path: 'marches/edit/:id', component: MarcheFormComponent },

      // Route Nantissements
      { path: 'nantissements', component: NantissementListComponent },

      // Route Garanties
      { path: 'garanties', component: GarantieListComponent },
      { path: 'problemes', component: ListeProblemesComponent },
 { path: 'penalites', component: PenaliteBlockComponent },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },

  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },

  // Wildcard redirection
  {
    path: '**',
    redirectTo: 'login'
  }
];
