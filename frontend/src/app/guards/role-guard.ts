import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { StorageService } from '../services/storage.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const storageService = inject(StorageService);
  const router = inject(Router);

  const user = storageService.getUser();
  const expectedRoles: string[] = route.data['expectedRoles'] || [];

  // 1. Si aucun utilisateur en session
  if (!user) {
    console.warn('⛔ [RoleGuard] Aucun utilisateur trouvé dans localStorage');
    router.navigate(['/login']);
    return false;
  }

  // 2. Extraire le rôle (gère 'role' et 'roles')
  const userRole: string = user.role || (Array.isArray(user.roles) ? user.roles[0] : '');

  // 3. Normaliser les rôles (supprimer 'ROLE_' si présent)
  const cleanUserRole = userRole.startsWith('ROLE_') ? userRole.replace('ROLE_', '') : userRole;
  const cleanExpectedRoles = expectedRoles.map(r => r.startsWith('ROLE_') ? r.replace('ROLE_', '') : r);

  console.log('🔍 [RoleGuard] Route demandée :', state.url);
  console.log('🔍 [RoleGuard] Rôle utilisateur :', cleanUserRole);
  console.log('🔍 [RoleGuard] Rôles attendus :', cleanExpectedRoles);

  // 4. Vérification de la permission
  if (cleanExpectedRoles.includes(cleanUserRole)) {
    return true;
  }

  console.warn('⛔ [RoleGuard] Accès refusé : Rôle non correspondant');
  router.navigate(['/login']);
  return false;
};
