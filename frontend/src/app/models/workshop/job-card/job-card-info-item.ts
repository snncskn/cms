import { User } from '../../general/user';
import { VehicleInfo } from '../../vehicle/vehicle-info';
import { JobCardInfo } from './job-card-info';
import { PartItem } from '../../item/item';

export class JobCardInfoItem {
    public uuid: string;
    public jobCardInfo: JobCardInfo;
    public itemInfo: PartItem;
    public jobCardItemStatus: string;
    public quantity: string;
    public approveQuantity: string;
    public availableQuantity: string;
    public deliveredQuantity: string;
    public remainingQuantity: string;
    public receivedUserInfo: User;
    public requestUserInfo: User;
    public supervisorUser: User;
    public requestDate: string;
    public deliveredUser: User;
    public deliveredDate: string;
    public description: string;
}
