import { Routes } from '@angular/router';

import { AdminLayoutComponent } from './layouts/admin/admin-layout.component';
import { AuthLayoutComponent } from './layouts/auth/auth-layout.component';

export const AppRoutes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
   {
    path: '',
    component: AdminLayoutComponent,
    children: [
      {
        path: '',
        loadChildren: './dashboard/dashboard.module#DashboardModule'
      }, {
        path: 'components',
        loadChildren: './components/components.module#ComponentsModule'
      }, {
        path: 'vehicle',
        loadChildren: './modules/vehicle/vehicle.module#Vehicle'
      }, {
        path: 'warehouse',
        loadChildren: './modules/warehouse/warehouse.module#Warehouse'
      }, {
        path: 'settings',
        loadChildren: './modules/settings/settings.module#Settings'
      },
      {
        path: 'workshop',
        loadChildren: './modules/workshop/workshop.module#Workshop'
      },
      {
        path: '',
        loadChildren: './userpage/user.module#UserModule'
      }
    ]
  }, {
    path: '',
    component: AuthLayoutComponent,
    children: [{
      path: 'pages',
      loadChildren: './pages/pages.module#PagesModule'
    }]
  }
];
