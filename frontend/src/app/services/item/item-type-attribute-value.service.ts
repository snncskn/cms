import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Observable, throwError} from 'rxjs';
import {Injectable} from '@angular/core';
import { VehicleAttributeForm } from 'src/app/models/map/vehicle-attribute-form';
import { Pagination } from 'src/app/models/pagination-generic';
import { VehicleAttribute } from 'src/app/models/vehicle/vehicle-attribute';
import { VehicleAttributeValueCreateUpdateForm } from 'src/app/models/item/item-attribute-value-create-update-form';
import { RestResponse } from 'src/app/models/general/rest-response';

@Injectable({
   providedIn: 'root'
})
export class ItemTypeAttributeValueService {
  private readonly endpoint = 'item-attribute-value';
  constructor(
      private readonly httpClient: HttpClient
  ) {

  }

  public save(atributes: VehicleAttributeValueCreateUpdateForm):
                Observable<RestResponse<VehicleAttributeValueCreateUpdateForm>> {

    return this.httpClient.post<RestResponse<VehicleAttributeValueCreateUpdateForm>>(
                                `${environment.url}/${this.endpoint}/`, atributes);
  }

  public delete(uuid: string): Observable<VehicleAttributeForm> {

    return this.httpClient.delete<VehicleAttributeForm>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
  }

  public find(uuid: string): Observable<VehicleAttributeForm> {

    return this.httpClient.get<VehicleAttributeForm>(`${environment.url}/${this.endpoint}/${uuid}`);
  }

  public list(): Observable<Pagination<VehicleAttribute[]>> {

    return this.httpClient
        .get<Pagination<VehicleAttribute[]>>(`${environment.url}/${this.endpoint}/list`);
  }

  private readonly formatErrors = (error: any) => {

    return throwError(error.error);
  }
}
