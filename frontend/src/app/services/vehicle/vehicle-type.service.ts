import { environment } from '../../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError, Observer } from 'rxjs';
import { Injectable } from '@angular/core';
import { VehicleTypeCreateUpdateForm } from '../../models/vehicle/vehicle-type-create-update-form';
import { VehicleType } from 'src/app/models/map/vehicle-type';
import { Pageable, Pagination } from 'src/app/models/pagination-generic';
import { VehicleTypeAttributes } from 'src/app/models/vehicle/vehicle-type-attributes';
import { RestResponse } from 'src/app/models/general/rest-response';

@Injectable({
  providedIn: 'root'
})
export class VehicleTypeService {

  private readonly endpoint = 'vehicle-type';

  constructor(
    private readonly httpClient: HttpClient
  ) {

  }
  public save(vehicle: VehicleTypeCreateUpdateForm): Observable<VehicleTypeCreateUpdateForm> {

    return this.httpClient.post<VehicleTypeCreateUpdateForm>(`${environment.url}/${this.endpoint}/`, vehicle);
  }

  public delete(uuid: string): Observable<VehicleTypeCreateUpdateForm> {

    return this.httpClient.delete<VehicleTypeCreateUpdateForm>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
  }

  public find(uuid: string): Observable<RestResponse<VehicleTypeAttributes>> {

    return this.httpClient.get<RestResponse<VehicleTypeAttributes>>(`${environment.url}/${this.endpoint}/${uuid}`);
  }

  public list(page: Pageable): Observable<Pagination<VehicleTypeCreateUpdateForm[]>> {

    return this.httpClient
      .get<Pagination<VehicleTypeCreateUpdateForm[]>>(
        `${environment.url}/${this.endpoint}?page=${page.page}&size=${page.size}`);
  }

  public list2(): Observable<Pagination<VehicleType[]>> {

    return this.httpClient
      .get<Pagination<VehicleType[]>>(`${environment.url}/${this.endpoint}`);
  }
  public filter(page: Pageable, filter: string): Observable<Pagination<VehicleTypeCreateUpdateForm[]>> {
    if (!filter) {
      filter = '*';
    }
    return this.httpClient
        .get<Pagination<VehicleTypeCreateUpdateForm[]>>
            (`${environment.url}/${this.endpoint}/filter/${filter}?page=${page.page}&size=${page.size}`);
}


  private readonly formatErrors = (error: any) => {

    return throwError(error.error);
  }
}
