import {NgModule}                         from '@angular/core';
import {RouterModule}                     from '@angular/router';
import {CommonModule}                     from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NouisliderModule}                 from 'ng2-nouislider';
import {TagInputModule}                   from 'ngx-chips';
import {MaterialModule}                   from '../../../app/app.module';
import {VehicleRoutes}                    from './vehicle.routing';
import {VehicleFormComponent}             from './vehicle-form/vehicle-form.component';
import {VehicleListComponent}             from './vehicle-list/vehicle-list.component';
import {VehicleCardComponent}             from './vehicle-card/vehicle-card.component';
import { FieldErrorVehicleComponent } from './field-error-display/field-error-display.component';
import { WebcamModule } from 'ngx-webcam';

@NgModule({
    declarations: [VehicleFormComponent, FieldErrorVehicleComponent,
                VehicleListComponent, VehicleCardComponent],
    imports: [
        CommonModule,
        RouterModule.forChild(VehicleRoutes),
        FormsModule,
        ReactiveFormsModule,
        NouisliderModule,
        TagInputModule,
        MaterialModule,
        WebcamModule
    ]
})

export class Vehicle {
}
