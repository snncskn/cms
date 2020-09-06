import { Routes } from '@angular/router';

import { VehicleFormComponent } from './vehicle-form/vehicle-form.component';
import { VehicleListComponent } from './vehicle-list/vehicle-list.component';
import { VehicleCardComponent } from './vehicle-card/vehicle-card.component';

export const VehicleRoutes: Routes = [
  {
    path: '',
    children: [{
      path: 'machine',
      component: VehicleFormComponent
    },
    {
      path: 'machine/:id',
      component: VehicleFormComponent
    },
    {
      path: 'card/:id',
      component: VehicleCardComponent
    },
    {
      path: 'vehicles',
      component: VehicleListComponent
    },
    {
      path: 'machines',
      component: VehicleListComponent
    },
    ]
  }
];
