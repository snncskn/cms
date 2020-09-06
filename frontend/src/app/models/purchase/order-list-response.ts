import { Order } from './order';
import { Data } from '../pagination-generic';

export class OrderListResponseInfo {
    public orderListItemProjections: Data<Order[]>;
    public requestCounts: number;
    public orderCounts: number;
    public invoiceCounts: number;
    public rejectCounts: number;
}
