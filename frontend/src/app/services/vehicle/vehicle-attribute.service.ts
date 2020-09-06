import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, throwError } from 'rxjs';
import { Pagination, Pageable } from '../../models/map/pagination';
import { Injectable } from '@angular/core';
import { VehicleAttributeForm } from '../../models/map/vehicle-attribute-form';
import { PaginationAttr } from 'src/app/models/map/pagination-attribute';
import { VehicleTypeCreateUpdateForm } from 'src/app/models/vehicle/vehicle-type-create-update-form';

@Injectable({
  providedIn: 'root'
})

export class VehicleAttributeService {
  private readonly endpoint = 'vehicle-attribute';
  constructor(
    private readonly httpClient: HttpClient
  ) {

  }
  public save(atributes: VehicleTypeCreateUpdateForm): Observable<VehicleAttributeForm> {

    return this.httpClient.post<VehicleAttributeForm>(`${environment.url}/${this.endpoint}/save/`, atributes);
  }

  public delete(uuid: string): Observable<VehicleAttributeForm> {

    return this.httpClient.delete<VehicleAttributeForm>(`${environment.url}/${this.endpoint}/delete/${uuid}`);
  }

  public find(uuid: string): Observable<VehicleAttributeForm> {

    return this.httpClient.get<VehicleAttributeForm>(`${environment.url}/${this.endpoint}/${uuid}`);
  }
  public list(page: Pageable): Observable<PaginationAttr> {

    return this.httpClient
      .get<PaginationAttr>(`${environment.url}/${this.endpoint}?page=${page.page}&size=${page.size}`);
  }
  private readonly formatErrors = (error: any) => {

    return throwError(error.error);
  }
}
