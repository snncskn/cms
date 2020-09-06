import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable ,  throwError, BehaviorSubject } from 'rxjs';
import { VehicleTypeAttributes } from 'src/app/models/map/pagination.type';
import { PaginationVehicle } from 'src/app/models/map/pagination.vehicle';
import { Pageable, Pagination } from 'src/app/models/pagination-generic';
import { RestResponse } from 'src/app/models/general/rest-response';
import { VehicleInfo, Vehicle } from 'src/app/models/vehicle/vehicle-info';
import { Order } from 'src/app/models/purchase/order';
import { OrderItem } from 'src/app/models/purchase/order-item';
import { OrderStatus } from 'src/app/models/purchase/order-status';
import { OrderItemMessage } from 'src/app/models/purchase/order-item-message';
import { ItemImage, ApproveQuantityItem } from 'src/app/models/item/item-info';
import { OrderInvoiceForm } from 'src/app/models/purchase/order-invoice';
import { OrderReturn } from 'src/app/models/purchase/order-return';
import { OrderFilter } from 'src/app/models/purchase/order-filter';
import { DateFilterForm } from 'src/app/models/purchase/order-date-filter';
import { OrderListResponseInfo } from 'src/app/models/purchase/order-list-response';
import { AuthenticationService } from '../general/authentication.service';
import { OrderItemDetailListItemProjection } from 'src/app/models/purchase/order-item-detail';
@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private readonly endpoint = 'order';

  constructor(
      private readonly httpClient: HttpClient,
      private readonly authenticationService: AuthenticationService
  ) {

  }

  public create(order: Order): Observable<RestResponse<Order>> {
    if (order.uuid === null) {
      return this.save(order);
    } else {
      return this.update(order);
    }
  }

  public save(order: Order): Observable<RestResponse<Order>> {

    return this.httpClient.post<RestResponse<Order>>(`${environment.url}/${this.endpoint}/`, order);
  }
  public update(order: Order): Observable<RestResponse<Order>> {

    return this.httpClient.put<RestResponse<Order>>
                    (`${environment.url}/${this.endpoint}?uuid=${order.uuid}`, order);
  }

  public approveReject(orderStatus: OrderStatus): Observable<RestResponse<OrderStatus>> {
    return this.httpClient.post<RestResponse<OrderStatus>>(`
    ${environment.url}/${this.endpoint}/approve-reject`, orderStatus);
  }
  public orderItem(order: OrderItem): Observable<RestResponse<OrderItem>> {
    if (order.uuid !== null) {
      return this.updateOrderItem(order);
    } else {
      return this.createOrderItem(order);

    }
  }

  public updateOrderItem(order: OrderItem): Observable<RestResponse<OrderItem>> {

    return this.httpClient.put<RestResponse<OrderItem>>(`
                            ${environment.url}/${this.endpoint}/update-order-item?orderItemUuid=${order.uuid}`, order);
  }
  public createOrderItem(order: OrderItem): Observable<RestResponse<OrderItem>> {

    return this.httpClient.post<RestResponse<OrderItem>>(`
                            ${environment.url}/${this.endpoint}/create-order-item`, order);
  }
  public deleteOrderItem(order: OrderItem): Observable<RestResponse<OrderItem>> {

    return this.httpClient.delete<RestResponse<OrderItem>>(`
            ${environment.url}/${this.endpoint}/delete-order-item?orderItemUuid=${order.uuid}`);
  }
  public delete(uuid: string): Observable<RestResponse<OrderItem>> {

    return this.httpClient.delete<RestResponse<OrderItem>>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
  }

  public find(uuid: string): Observable<RestResponse<Order>> {

    return this.httpClient.get<RestResponse<Order>>(`${environment.url}/${this.endpoint}/${uuid}`);
  }
  public findOrderItemDetail(uuid: string): Observable<Pagination<OrderItemDetailListItemProjection[]>> {

    return this.httpClient.get<Pagination<OrderItemDetailListItemProjection[]>>
          (`${environment.url}/${this.endpoint}/find-order-item-detail/${uuid}?page=0&size=999`);
  }

  public list(page: Pageable): Observable<Pagination<Order[]>> {

    return this.httpClient
        .get<Pagination<Order[]>>(`${environment.url}/${this.endpoint}?page=${page.page}&size=${page.size}`);
  }

  public filter(filter: OrderFilter): Observable<RestResponse<OrderListResponseInfo>> {
    return this.httpClient.post<RestResponse<OrderListResponseInfo>>
      (`${environment.url}/${this.endpoint}/list-all-order/`, filter);
  }


  public createOrderItemMessage(message: OrderItemMessage): Observable<ItemImage> {

    return this.httpClient.post<ItemImage>(`${environment.url}/${this.endpoint}/create-order-item-message`, message);
  }
  public orderItemMessageList(page: Pageable, orderItemUuid: string):  Observable<Pagination<OrderItemMessage[]>> {

    return this.httpClient
    .get<Pagination<OrderItemMessage[]>>
    (`${environment.url}/${this.endpoint}/list-order-item-messages/` +
                        `${orderItemUuid}?page=${page.page}&size=${page.size}`);

  }

  public updateInvoiceInfo(orderInvoiceForm: OrderInvoiceForm): Observable<RestResponse<OrderStatus>> {
    return this.httpClient.put<RestResponse<OrderStatus>>(`
    ${environment.url}/${this.endpoint}/update-invoice-info?orderUuid=${orderInvoiceForm.uuid}`, orderInvoiceForm);
  }

  public createApproveQuantity(item: ApproveQuantityItem): Observable<RestResponse<OrderReturn>> {

    return this.httpClient.post<RestResponse<OrderReturn>>
              (`${environment.url}/${this.endpoint}/create-approve-quantity`, item);
  }
  public orderCount(item: DateFilterForm): Observable<number> {
    return this.httpClient.post<number>
              (`${environment.url}/${this.endpoint}/order-counts`, item);
  }
  public xls(filter: OrderFilter): Observable<Blob> {
    filter.size = 99999;
    filter.page = 0;
    const currentUser = this.authenticationService.currentUserValue;
    return this.httpClient.post(`${environment.url}/${this.endpoint}/xls`, filter, {
      headers: {
        Authorization: `Bearer ${currentUser.token}`,
        Site: ` ${currentUser.currentSite.uuid}`
      },
      responseType: 'blob'
    });
  }

  private readonly formatErrors: (error: any) => Observable<never> = (error: any) => {
    return throwError(error.error);
  }
}
