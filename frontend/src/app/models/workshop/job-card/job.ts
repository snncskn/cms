import { User } from '../../general/user';

export class Job {
    public requestDate: string;
    public deliveryTime: string;
    public deliveredBy: string;
    public recievedBy: string;
    public partNumber: string;
    public description: string;
    public status:  string;
    public availableQty: string;
    public Qty: string;
    public requestUser: User;
}
