import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WarehouseRoutes } from './workshop.routing';
import { MaterialModule } from '../../app.module';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NouisliderModule } from 'ng2-nouislider';
import { TagInputModule } from 'ngx-chips';
import { NgxBarcodeModule } from 'ngx-barcode';
import { NgxMaskModule } from 'ngx-mask';
import { WorkshopComponent } from './workshop/workshop.component';
import { BreakDownComponent } from './break-down/break-down.component';
import { JobCardComponent } from './job-card/job-card.component';
import { JobCardPdfComponent } from './job-card-pdf/job-card-pdf.component';
import { FieldErrorWorkshopComponent } from './field-error-display/field-error-display.component';
import { JobBreakdownPdfComponent } from './job-breakdown-pdf/job-breakdown-pdf.component';



@NgModule({
  declarations: [WorkshopComponent, BreakDownComponent,
    FieldErrorWorkshopComponent, JobCardComponent, JobCardPdfComponent, JobBreakdownPdfComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(WarehouseRoutes),
    FormsModule,
    ReactiveFormsModule,
    NouisliderModule,
    TagInputModule,
    NgxBarcodeModule,
    MaterialModule,
    NgxMaskModule.forRoot(),
  ],
  // providers: [
  //   { provide: RouteReuseStrategy, useClass: CustomReuseStrategy },
  // ],
})
export class Workshop { }
