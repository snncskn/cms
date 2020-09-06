import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StockComponent } from './stock/stock.component';
import { PartRequestsComponent } from './part-requests/part-requests.component';
import { WarehouseRoutes } from './warehouse.routing';
import { MaterialModule } from '../../../app/app.module';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NouisliderModule } from 'ng2-nouislider';
import { TagInputModule } from 'ngx-chips';
import {ItemsListComponent} from './item/item-list/items-list.component';
import {ItemFormComponent} from './item/item-form/item-form.component';
import { SupplierFormComponent } from './suppliers/supplier-form/supplier-form.component';
import { SupplierListComponent } from './suppliers/supplier-list/supplier-list.component';
import { PurchasesComponent } from './purchase/purchase-list/purchases.component';
import { PurchaseComponent } from './purchase/purchase-form/purchase.component';
import { NgxBarcodeModule } from 'ngx-barcode';
import { NgxMaskModule } from 'ngx-mask';
import { PurchasePdfComponent } from './purchase/purchase-pdf/purchase-pdf.component';
import { FieldErrorWarehouseComponent } from './field-error-display/field-error-display.component';
import { RequestComponent } from './request/request.component';
import { TransferFormComponent } from './transfer/transfer-form/transfer-form.component';
import { TransferListComponent } from './transfer/transfer-list/transfer-list.component';
import { StockListComponent } from './stock-list/stock-list.component';
import { SupplierModalComponent } from './suppliers/supplier-modal/supplier-modal.component';


@NgModule({
  declarations: [PartRequestsComponent, StockComponent,
          PurchasesComponent,  PurchaseComponent,
          SupplierListComponent, FieldErrorWarehouseComponent,
          SupplierFormComponent, ItemsListComponent, SupplierModalComponent,
          ItemFormComponent, PurchasePdfComponent, RequestComponent,
          TransferFormComponent, TransferListComponent, StockListComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(WarehouseRoutes),
    FormsModule,
    ReactiveFormsModule,
    NouisliderModule,
    TagInputModule,
    NgxBarcodeModule,
    MaterialModule,
    NgxMaskModule.forRoot()
    ],
})
export class Warehouse { }
