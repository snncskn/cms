import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NouisliderModule } from 'ng2-nouislider';
import { TagInputModule } from 'ngx-chips';
import { MaterialModule } from '../../../app/app.module';
import { SettingsRoutes } from './settings.routing';
import { VehicleTypeComponent } from './vehicle-type/vehicle-form/vehicle-type.component';
import { AttributesFormComponent } from './vehicle-attributes/attributes-form/attributes-form.component';
import { VehicleAttributesComponent } from './vehicle-attributes/attributes-list/vehicle-attributes.component';
import {VehicleTypeListComponent} from './vehicle-type/vehicle-list/vehicle-type.component';
import { PartAttributeFormComponent } from './item-attributes/attributes-form/part-attribute.component';
import { PartAttributesComponent } from './item-attributes/attributes-list/part-attributes.component';
import { ItemsTypeListComponent } from './item-type/item-list/items-type.component';
import { ItemTypeComponent } from './item-type/item-form/item-type.component';
import {UserCreateComponent} from './users/user-create/user-create.component';
import {UserListComponent} from './users/user-list/user-list.component';
import { SitesFormComponent } from './sites/sites-form/sites-form.component';
import { SitesListComponent } from './sites/sites-list/sites-list.component';
import { PropertyListComponent } from './property/property-list/property-list.component';
import { PropertyFormComponent } from './property/property-form/property-form.component';
import { NgxMaskModule } from 'ngx-mask';
import { RoleListComponent } from './roles/role-list/role-list.component';
import { RoleFormComponent } from './roles/role-form/role-form.component';
import { FieldErrorSettingsComponent } from './field-error-display/field-error-display.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(SettingsRoutes),
    FormsModule,
    ReactiveFormsModule,
    NouisliderModule,
    TagInputModule,
    MaterialModule,
    NgxMaskModule,
  ],
  declarations: [VehicleAttributesComponent,
    VehicleTypeComponent,
    VehicleTypeListComponent,
    AttributesFormComponent,
    ItemsTypeListComponent,
    ItemTypeComponent,
    PartAttributeFormComponent,
    PartAttributesComponent,
    UserCreateComponent,
    UserListComponent,
    SitesFormComponent,
    SitesListComponent,
    PropertyFormComponent,
    PropertyListComponent,
    RoleListComponent,
    RoleFormComponent,
    FieldErrorSettingsComponent
    ]
})

export class Settings {}
