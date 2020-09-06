import { SiteForm } from '../sites/site-form';
import { OrderItem } from '../purchase/order-item';

export class TransferCreateUpdateForm {
    public uuid: string;
    public sourceSiteUuid: string;
    public targetSiteUuid: string;
    public status: string;
    public transferNumber: string;
    public totalQuantity: string;
    public transferDate: string;

    public targetSiteInfo: SiteForm;
    public sourceSiteInfo: SiteForm;

    public transferItemInfos: OrderItem[];
}
