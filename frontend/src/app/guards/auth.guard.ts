import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.hasToken()) {
    return true;
  }

  return router.createUrlTree(['/login']);
};

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Must have token AND be admin
  if (authService.hasToken()) {
    if (authService.isAdmin()) {
      return true;
    }
    // Has token but not admin - redirect to client
    return router.createUrlTree(['/client']);
  }

  // No token - redirect to login
  return router.createUrlTree(['/login']);
};

export const clientGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.hasToken()) {
    // No token - redirect to login
    return router.createUrlTree(['/login']);
  }

  if (authService.isAdmin()) {
    // Admin users should not access client dashboard
    return router.createUrlTree(['/admin']);
  }

  // Has token and is not admin - allow access
  return true;
};

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.hasToken()) {
    return true;
  }

  const redirectPath = authService.isAdmin() ? '/admin' : '/client';
  return router.createUrlTree([redirectPath]);
};
