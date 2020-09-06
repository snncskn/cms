import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {environment} from '../../../environments/environment';
import {catchError} from 'rxjs/operators';
import { RestResponse } from 'src/app/models/general/rest-response';
import { Pagination } from 'src/app/models/pagination-generic';
import { TransferCreateUpdateForm } from 'src/app/models/transfer/transfer-create-form';
import { TransferItemCreateUpdateForm } from 'src/app/models/transfer/transfer-item-create';
import { TransferFilterForm } from 'src/app/models/transfer/transfer-filter';
import { TransferListItemProjection, TransferListResponseInfo } from 'src/app/models/transfer/transfer-item';
import { OrderItem } from 'src/app/models/purchase/order-item';
import { OrderStatus } from 'src/app/models/purchase/order-status';
import { ApproveQuantityItem } from 'src/app/models/item/item-info';
import { OrderReturn } from 'src/app/models/purchase/order-return';
import { DateFilterForm } from 'src/app/models/purchase/order-date-filter';
import { AuthenticationService } from '../general/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class TransferService {
  private readonly endpoint = 'transfer';
  constructor(
      private readonly httpClient: HttpClient,
      private readonly authenticationService: AuthenticationService
  ) { }

  public save(item: TransferCreateUpdateForm): Observable<RestResponse<TransferCreateUpdateForm>> {

    return this.httpClient.post<RestResponse<TransferCreateUpdateForm>>
                                (`${environment.url}/${this.endpoint}/`, item);
  }
  public update(item: TransferCreateUpdateForm): Observable<RestResponse<TransferCreateUpdateForm>> {

    return this.httpClient.put<RestResponse<TransferCreateUpdateForm>>
                                (`${environment.url}/${this.endpoint}?uuid=${item.uuid}`, item);
  }
  public delete(uuid: string): Observable<TransferCreateUpdateForm> {

    return this.httpClient.delete<TransferCreateUpdateForm>
                              (`${environment.url}/${this.endpoint}?uuid=${uuid}`);
  }

  public find(uuid: string): Observable<RestResponse<TransferCreateUpdateForm>> {

    return this.httpClient.get<RestResponse<TransferCreateUpdateForm>>
                                    (`${environment.url}/${this.endpoint}/${uuid}`);
  }
  public createTransferItem(item: TransferItemCreateUpdateForm): Observable<TransferItemCreateUpdateForm> {

    return this.httpClient.post<TransferItemCreateUpdateForm>
              (`${environment.url}/${this.endpoint}/create-transfer-item`, item);
  }
  public get(path: string, params: HttpParams = new HttpParams()): Observable<any> {

    return this.httpClient.get(`${environment.url}${path}`, { params })
        .pipe(catchError(this.formatErrors));
  }

  public put(path: string, body: Object = {}): Observable<any> {

    return this.httpClient.put(
        `${environment.url}${path}`,
        JSON.stringify(body)
    ).pipe(catchError(this.formatErrors));
  }

  public post(path: string, body: Object = {} ): Observable<any> {

    return this.httpClient.post(
        `${environment.url}${path}`,
        JSON.stringify(body)
    ).pipe(catchError(this.formatErrors));
  }

  public list(): Observable<Pagination<TransferCreateUpdateForm[]>> {

    return this.httpClient
        .get<Pagination<TransferCreateUpdateForm[]>>(`${environment.url}/${this.endpoint}`);
  }

  public filter(filter: TransferFilterForm): Observable<RestResponse<TransferListResponseInfo>> {
    return this.httpClient.post<RestResponse<TransferListResponseInfo>>
      (`${environment.url}/${this.endpoint}/list-all-transfer/`, filter);
  }


  public xls(filter: TransferFilterForm): Observable<Blob> {
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

  public deleteTransferItem(order: OrderItem): Observable<RestResponse<OrderItem>> {

    return this.httpClient.delete<RestResponse<OrderItem>>(`
            ${environment.url}/${this.endpoint}/delete-transfer-item?transferItemUuid=${order.uuid}`);
  }
  public approveReject(orderStatus: OrderStatus): Observable<RestResponse<OrderStatus>> {
    return this.httpClient.post<RestResponse<OrderStatus>>(`
    ${environment.url}/${this.endpoint}/approve-reject`, orderStatus);
  }
  public createApproveQuantity(item: ApproveQuantityItem): Observable<OrderReturn> {

    return this.httpClient.post<OrderReturn>
              (`${environment.url}/${this.endpoint}/create-approve-quantity`, item);
  }
  public transferCount(item: DateFilterForm): Observable<number> {
    return this.httpClient.post<number>
              (`${environment.url}/${this.endpoint}/order-counts`, item);
  }
  public findAvailableQuantity(itemUuid: string, siteUuid: string): Observable<RestResponse<number>> {
    return this.httpClient.get<RestResponse<number>>
              (`${environment.url}/${this.endpoint}/find-available-quantity/${siteUuid}/${itemUuid}`);
  }

  private readonly formatErrors: (error: any) => Observable<never> = (error: any) => {
    return throwError(error.error);
  }
}
