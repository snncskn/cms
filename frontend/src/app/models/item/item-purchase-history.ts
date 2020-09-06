import { UserInfo } from '../users/user-info';

export class ItemPurchasehistory {
    public itemUuid: string;
    public requestDate: string;
    public invoiceNummber: string;
    public quantity: string;
    public total: string;
    public supplierName: UserInfo;
    public startDate: string;
    public endDate: string;
    public page: number;
    public size: number;
    public filter: string;
}
