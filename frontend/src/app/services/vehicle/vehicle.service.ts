import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { VehicleTypeAttributes } from 'src/app/models/map/pagination.type';
import { PaginationVehicle } from 'src/app/models/map/pagination.vehicle';
import { Pageable, Pagination } from 'src/app/models/pagination-generic';
import { RestResponse, RestResponseEntity } from 'src/app/models/general/rest-response';
import { VehicleInfo, Vehicle, VehicleImage } from 'src/app/models/vehicle/vehicle-info';
import { Image } from 'src/app/models/general/image';
import { VehicleFilter } from 'src/app/models/vehicle/vehicle-filter';
import { BreakDown } from 'src/app/models/workshop/break-down/break-down';
import { BreakDownSearch } from 'src/app/models/workshop/break-down/break-down-search';
import { AuthenticationService } from '../general/authentication.service';
import { VehicleUsedItemFilterForm } from 'src/app/models/vehicle/vehicle-item-filter';
import { VehicleItemInfo } from 'src/app/models/vehicle/vehicle-item-info';
@Injectable({
  providedIn: 'root'
})
export class VehicleService {
  private readonly endpoint = 'vehicle';
  private readonly imageEndpoint = 'image';
  constructor(
    private readonly httpClient: HttpClient,
    private readonly authenticationService: AuthenticationService
  ) {

  }
  public save(vehicle: Vehicle): Observable<PaginationVehicle> {

    return this.httpClient.post<PaginationVehicle>(`${environment.url}/${this.endpoint}/`, vehicle);
  }
  public createVehicleList(vehicleAttr: VehicleTypeAttributes): Observable<Vehicle> {

    return this.httpClient.post<Vehicle>(`${environment.url}/${this.endpoint}/createVehicleList`, vehicleAttr);
  }
  public createImageVehicle(imgInfo: VehicleImage): Observable<Vehicle> {

    return this.httpClient.post<Vehicle>(`${environment.url}/${this.endpoint}/createImages`, imgInfo);
  }

  public image(file: FormData): Observable<FormData> {
    return this.httpClient.post<FormData>(`${environment.url}/${this.imageEndpoint}`, file);
  }

  public delete(uuid: string): Observable<RestResponse<Vehicle>> {

    return this.httpClient.delete<RestResponse<Vehicle>>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
  }

  public find(uuid: string): Observable<RestResponse<Vehicle>> {

    return this.httpClient.get<RestResponse<Vehicle>>(`${environment.url}/${this.endpoint}/${uuid}`);
  }

  public findVehicleList(uuid: string): Observable<RestResponse<VehicleInfo>> {

    return this.httpClient.get<RestResponse<VehicleInfo>>(
      `${environment.url}/${this.endpoint}/findVehicleList/${uuid}`);
  }
  public list(page: Pageable): Observable<Pagination<Vehicle[]>> {

    return this.httpClient
      .get<Pagination<Vehicle[]>>(`${environment.url}/${this.endpoint}?page=${page.page}&size=${page.size}`);
  }
  public filter(page: Pageable, filter: VehicleFilter): Observable<Pagination<Vehicle[]>> {
    return this.httpClient
      .post<Pagination<Vehicle[]>>(
        `${environment.url}/${this.endpoint}/filter?page=${page.page}&size=${page.size}`, filter);

  }
  public findAllBreakdown(breakDownSearch: BreakDownSearch): Observable<Pagination<BreakDown[]>> {
    return this.httpClient.post<Pagination<BreakDown[]>>
      (`${environment.url}/${this.endpoint}/find-all-breakdown/`, breakDownSearch);
  }

  public findAllUsedItem(filter: VehicleUsedItemFilterForm): Observable<Pagination<VehicleItemInfo[]>> {
    return this.httpClient.post<Pagination<VehicleItemInfo[]>>
      (`${environment.url}/${this.endpoint}/find-all-used-item/`, filter);
  }

  public xls(page: Pageable, filter: VehicleFilter): Observable<RestResponse<string>> {
    filter.size = 99999;
    filter.page = 0;
    return this.httpClient
      .post<RestResponse<string>>(
        `${environment.url}/${this.endpoint}/xlsExcel`, filter);
  }
  public postForFile(filter: VehicleFilter): Observable<Blob> {
    filter.size = 99999;
    filter.page = 0;
    const currentUser = this.authenticationService.currentUserValue;
    return this.httpClient.post(`${environment.url}/${this.endpoint}/xlsExcel`, filter, {
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
