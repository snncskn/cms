import { Routes } from '@angular/router';

import { DashboardComponent } from './dashboard.component';
import { ProfileComponent } from './profile.component';

export const DashboardRoutes: Routes = [
  {
    path: '',
    children: [{
      path: 'dashboard',
      component: DashboardComponent
    },
    {
      path: 'profile',
      component: ProfileComponent
    }
    ]
  }
];
