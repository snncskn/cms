import { OrderItem } from './order-item';

export class OrderItemDetailListItemProjection {
    public uuid: string;
    public orderItem: OrderItem;
    public quantity: string;
    public description: string;
    public createdBy: string;
    public createdAt: string;
    public createdDate: string;

}
