import { OrderItem } from './order-item';
import { SupplierForm } from '../suppliers/supplier-form';
import { SiteForm } from '../sites/site-form';
import { Item } from '../item/item-info';
import { BasicUserInfo } from './order-basic';

export class Order {
    public uuid: string;
    public supplierUuid: string;
    public siteUuid: string;
    public requestDate: string;
    public requestNumber: string;
    public orderNumber: string;
    public invoiceDate: string;
    public invoiceNumber: string;
    public totalQuantity: number;
    public grandTotal: number;
    public taxTotal: number;
    public discountTotal: number;
    public totalAmount: number;
    public status: string;
    public orderCreationDate: string;
    public orderItemInfos: OrderItem[];
    public supplier: SupplierForm[];
    public supplierInfo: SupplierForm;
    public basicUserInfo: BasicUserInfo;
    public siteInfo: SiteForm;
    public itemInfo: Item;
    public addressDetail: string;


}
