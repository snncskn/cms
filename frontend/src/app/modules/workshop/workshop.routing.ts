import { Routes } from '@angular/router';
import { WorkshopComponent } from './workshop/workshop.component';
import { BreakDownComponent } from './break-down/break-down.component';
import { JobCardComponent } from './job-card/job-card.component';
import { JobCardPdfComponent } from './job-card-pdf/job-card-pdf.component';
import {JobBreakdownPdfComponent} from './job-breakdown-pdf/job-breakdown-pdf.component';
export const WarehouseRoutes: Routes = [

  {
    path: '',
    children: [
      {
        path: 'list',
        component: WorkshopComponent
      },
      {
        path: 'break-down/:id',
        component: BreakDownComponent
      },
      {
        path: 'job-card/:id',
        component: JobCardComponent
      },
      {
        path: 'job-card-pdf/:id',
        component: JobCardPdfComponent
      },
      {
        path: 'job-breakdown-pdf/:id',
        component: JobBreakdownPdfComponent
      }
    ]
  }
];
