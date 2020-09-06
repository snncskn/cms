import { SiteForm } from '../sites/site-form';
import { SupplierCreateUpdateForm } from '../suppliers/supplier-create-update-form';
import { Item } from '../item/item-info';


export class OrderItem {
    public uuid?: string;
    public itemUuid: string;
    public orderUuid?: string;
    public description?: string;
    public barcode?: string;
    public quantity?: number;
    public unitPrice?: string;
    public discountPercent?: number;
    public discount?: number;
    public taxPercent?: number;
    public totalQuantity?: number;
    public total?: number;
    public taxTotal?: number;
    public siteInfo?: SiteForm;
    public supplierInfo?: SupplierCreateUpdateForm;
    public itemInfo: Item;
    public unit: string;
    public approveQuantity: number;
    public approve: boolean;

}
