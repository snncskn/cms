import { Routes } from '@angular/router';
import { VehicleTypeComponent } from './vehicle-type/vehicle-form/vehicle-type.component';
import {VehicleTypeListComponent} from './vehicle-type/vehicle-list/vehicle-type.component';
import { AttributesFormComponent } from './vehicle-attributes/attributes-form/attributes-form.component';
import { VehicleAttributesComponent } from './vehicle-attributes/attributes-list/vehicle-attributes.component';
import { PartAttributesComponent } from './item-attributes/attributes-list/part-attributes.component';
import { PartAttributeFormComponent } from './item-attributes/attributes-form/part-attribute.component';
import { ItemTypeComponent } from './item-type/item-form/item-type.component';
import { ItemsTypeListComponent } from './item-type/item-list/items-type.component';
import {UserCreateComponent} from './users/user-create/user-create.component';
import {UserListComponent} from './users/user-list/user-list.component';
import {SitesListComponent} from './sites/sites-list/sites-list.component';
import {SitesFormComponent} from './sites/sites-form/sites-form.component';
import { PropertyListComponent } from './property/property-list/property-list.component';
import { PropertyFormComponent } from './property/property-form/property-form.component';
import {RoleListComponent} from './roles/role-list/role-list.component';
import {RoleFormComponent} from './roles/role-form/role-form.component';

export const SettingsRoutes: Routes = [

    {
        path: '',
        children: [
            {
                path: 'vehicle-type',
                component: VehicleTypeComponent
            },
            {
                path: 'vehicle-type/:id',
                component: VehicleTypeComponent
            },
            {
                path: 'vehicle-types',
                component: VehicleTypeListComponent
            },
            {
                path: 'vehicle-attributes',
                component: VehicleAttributesComponent
            },
            {
                path: 'vehicle-attribute',
                component: AttributesFormComponent
            },
            {
                path: 'vehicle-attribute/:id',
                component: AttributesFormComponent
            },
            {
                path: 'item-type',
                component: ItemTypeComponent
            },
            {
                path: 'item-type/:id',
                component: ItemTypeComponent
            },
            {
                path: 'item-types',
                component: ItemsTypeListComponent
            },

            {
                path: 'item-attribute',
                component: PartAttributeFormComponent
            },
            {
                path: 'item-attribute/:id',
                component: PartAttributeFormComponent
            },
            {
                path: 'item-attributes',
                component: PartAttributesComponent
            },
            {
                path: 'user',
                component: UserCreateComponent
            },
            {
                path: 'user/:id',
                component: UserCreateComponent
            },
            {
                path: 'users',
                component: UserListComponent
            },
            {
                path: 'site',
                component: SitesFormComponent
            },
            {
                path: 'site/:id',
                component: SitesFormComponent
            },
            {
                path: 'sites',
                component: SitesListComponent
            },
            {
                path: 'propertys',
                component: PropertyListComponent
            },
            {
                path: 'property',
                component: PropertyFormComponent
            },
            {
                path: 'property/:id',
                component: PropertyFormComponent
            },
            {
                path: 'roles',
                component: RoleListComponent
            },
            {
                path: 'role',
                component: RoleFormComponent
            },
            {
                path: 'role/:id',
                component: RoleFormComponent
            },



        ]
    }
    ];
