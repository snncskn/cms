import { SiteForm } from '../sites/site-form';
import { OrderStatus } from '../purchase/order-status';
import { UserForm } from '../users/userForm';
import { Pagination, Data } from 'src/app/models/pagination-generic';

export class TransferListItemProjection {
  public uuid: string;
  public targetSite: SiteForm;
  public status: OrderStatus;
  public transferNumber: string;
  public rejectionReason: string;
  public transferDate: string;
  public totalQuantity: string;
  public deliverDate: string;
  public requestDate: string;
  public sourceOwner: UserForm;
  public sourceSite: SiteForm;
  public targetOwner: SiteForm;
}

export class TransferListResponseInfo {
  public transferListItemProjections: Data<TransferListItemProjection[]>;
  public requestCounts: number;
  public transferCounts: number;
  public deliverCounts: number;
  public rejectCounts: number;
}
