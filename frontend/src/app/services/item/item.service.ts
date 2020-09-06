import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable ,  throwError } from 'rxjs';
import { VehicleTypeAttributes } from 'src/app/models/map/pagination.type';
import { Pageable, Pagination } from 'src/app/models/pagination-generic';
import { RestResponse } from 'src/app/models/general/rest-response';
import { PartItem } from 'src/app/models/item/item';
import { ItemInfo, Item, ItemImage, ApproveQuantityItem } from 'src/app/models/item/item-info';
import { Router } from '@angular/router';
import { ItemPurchasehistory } from 'src/app/models/item/item-purchase-history';
import { AuthenticationService } from '../general/authentication.service';
import { VehicleFilter } from 'src/app/models/vehicle/vehicle-filter';
import { ItemFilterForm } from 'src/app/models/item/item-fliter';
@Injectable({
  providedIn: 'root'
})
export class ItemService {
  public isPrinting = false;

  private readonly endpoint = 'item';
  private readonly imageEndpoint = 'image';

  constructor(
      private readonly httpClient: HttpClient,
      private readonly router: Router,
      private readonly authenticationService: AuthenticationService

  ) {

  }
  public save(item: Item): Observable<RestResponse<Item>> {

    return this.httpClient.post<RestResponse<Item>>(`${environment.url}/${this.endpoint}/`, item);
  }
  public createItemList(vehicleAttr: VehicleTypeAttributes): Observable<Item> {

    return this.httpClient.post<Item>(`${environment.url}/${this.endpoint}/createItemList`, vehicleAttr);
  }

  public image(file: FormData): Observable<FormData> {
    return this.httpClient.post<FormData>(`${environment.url}/${this.imageEndpoint}`, file);
  }

  public delete(uuid: string): Observable<RestResponse<Item>> {

    return this.httpClient.delete<RestResponse<Item>>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
  }

  public find(uuid: string): Observable<RestResponse<Item>> {

    return this.httpClient.get<RestResponse<Item>>(`${environment.url}/${this.endpoint}/${uuid}`);
  }
  public findItemList(uuid: string): Observable<RestResponse<ItemInfo>> {

    return this.httpClient.get<RestResponse<ItemInfo>>(
                              `${environment.url}/${this.endpoint}/findItemList/${uuid}`);
  }

  public list(page: Pageable): Observable<Pagination<PartItem[]>> {

    return this.httpClient
        .get<Pagination<PartItem[]>>(`${environment.url}/${this.endpoint}?page=${page.page}&size=${page.size}`);
  }
  public barcode(): Observable<Pagination<PartItem[]>> {
    return this.httpClient
        .get<Pagination<PartItem[]>>(`${environment.url}/${this.endpoint}/newBarcode`);
  }
  public filter(page: Pageable, filter: string): Observable<Pagination<PartItem[]>> {

    return this.httpClient
        .get<Pagination<PartItem[]>>(
            `${environment.url}/${this.endpoint}/filter/${filter}?page=${page.page}&size=${page.size}`
        );
  }
  public filterAuto(filter: string): Observable<Pagination<PartItem[]>> {
    const f = new ItemFilterForm();
    f.filter = filter;
    f.size = 9;
    f.page = 0;
    return this.httpClient
        .post<Pagination<PartItem[]>>(
            `${environment.url}/${this.endpoint}/filter`, f
        );
  }
  public filterPost(filter: ItemFilterForm): Observable<Pagination<PartItem[]>> {

    return this.httpClient
        .post<Pagination<PartItem[]>>(
            `${environment.url}/${this.endpoint}/filter`, filter
        );
  }
  public xls(filter: ItemFilterForm): Observable<Blob> {
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

  public createVehicleList(vehicleAttr: VehicleTypeAttributes): Observable<Item> {

    return this.httpClient.post<Item>(`${environment.url}/${this.endpoint}/createItemList`, vehicleAttr);
  }
  public createImageVehicle(imgInfo: ItemImage): Observable<ItemImage> {

    return this.httpClient.post<ItemImage>(`${environment.url}/${this.endpoint}/createImages`, imgInfo);
  }
  public findAllItemOrder(filter: ItemPurchasehistory): Observable<Pagination<ItemPurchasehistory[]>> {
    return this.httpClient.post<Pagination<ItemPurchasehistory[]>>
      (`${environment.url}/${this.endpoint}/find-all-item-order/`, filter);
  }

  private readonly formatErrors: (error: any) => Observable<never> = (error: any) => {
    return throwError(error.error);
  }
}
