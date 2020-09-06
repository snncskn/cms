import { Routes } from '@angular/router';
import {PartRequestsComponent} from './part-requests/part-requests.component';
import {StockComponent} from './stock/stock.component';
import {ItemsListComponent} from './item/item-list/items-list.component';
import {ItemFormComponent} from './item/item-form/item-form.component';
import {SupplierListComponent} from './suppliers/supplier-list/supplier-list.component';
import {SupplierFormComponent} from './suppliers/supplier-form/supplier-form.component';
import { PurchasesComponent } from './purchase/purchase-list/purchases.component';
import { PurchaseComponent } from './purchase/purchase-form/purchase.component';
import { PurchasePdfComponent } from './purchase/purchase-pdf/purchase-pdf.component';
import { RequestComponent } from './request/request.component';
import { TransferListComponent } from './transfer/transfer-list/transfer-list.component';
import { TransferFormComponent } from './transfer/transfer-form/transfer-form.component';
import { StockListComponent } from './stock-list/stock-list.component';

export const WarehouseRoutes: Routes = [

  {
    path: '',
    children: [
      {
        path: 'partRequests',
        component: PartRequestsComponent
      },
      {
        path: 'stock',
        component: StockComponent
      },
      {
        path: 'items',
        component: ItemsListComponent
      },

      {
        path: 'purchases',
        component: PurchasesComponent
      },
      {
        path: 'purchase',
        component: PurchaseComponent
      },
      {
        path: 'purchase/:id',
        component: PurchaseComponent
      },
      {
        path: 'purchase-pdf/:id',
        component: PurchasePdfComponent
      },
      {
        path: 'suppliers',
        component: SupplierListComponent
      },
      {
        path: 'supplier',
        component: SupplierFormComponent
      },
      {
        path: 'supplier/:id',
        component: SupplierFormComponent
      },
      {
        path: 'purchase',
        component: PurchaseComponent
      },
      {
        path: 'purchase/:id',
        component: PurchaseComponent
      },
      {
        path: 'item',
        component: ItemFormComponent
      },
      {
        path: 'item/:id',
        component: ItemFormComponent
      },
      {
        path: 'items',
        component: ItemsListComponent
      },
      {
        path: 'request',
        component: RequestComponent
      },
      {
        path: 'transfers',
        component: TransferListComponent
      },
      {
        path: 'transfer/:id',
        component: TransferFormComponent
      },
      {
        path: 'transfer',
        component: TransferFormComponent
      },
      {
        path: 'stocks',
        component: StockListComponent
      },

    ]
  }
];
