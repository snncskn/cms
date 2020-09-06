import { UserInfo } from '../users/user-info';

export class JobCardItemDeliveredListInfo {
    public jobCardItemUuid: string;
    public remainingQuantity: string;
    public deliveredQuantity: string;
    public deliveredDate: string;
    public deliveredUserInfo: UserInfo;
    public receivedUserInfo: UserInfo;
}
