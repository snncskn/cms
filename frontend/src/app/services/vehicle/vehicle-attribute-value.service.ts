import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, throwError } from 'rxjs';
import { Pageable } from '../../models/map/pagination';
import { Injectable } from '@angular/core';
import { VehicleAttributeForm } from '../../models/map/vehicle-attribute-form';
import { PaginationAttrValue } from 'src/app/models/map/pagination-attribute-value';
import { VehicleTypeCreateUpdateForm } from 'src/app/models/vehicle/vehicle-type-create-update-form';

@Injectable({
  providedIn: 'root'
})

export class VehicleAttributeValueService {

  private readonly endpoint = 'vehicle-attribute-value';

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
  public list(page: Pageable): Observable<PaginationAttrValue> {

    return this.httpClient
        .get<PaginationAttrValue>(`${environment.url}/${this.endpoint}?page=${page.page}&size=${page.size}`);
  }

  private readonly formatErrors = (error: any) => {

    return throwError(error.error);
  }
}
