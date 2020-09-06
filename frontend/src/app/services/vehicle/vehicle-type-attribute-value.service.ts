 import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Observable, throwError} from 'rxjs';
 import {Injectable} from '@angular/core';
 import { VehicleAttributeValueCreateUpdateForm } from 'src/app/models/map/vehicle-attribute-value-create-update-form';
import { VehicleAttributeForm } from 'src/app/models/map/vehicle-attribute-form';
import { Pagination } from 'src/app/models/pagination-generic';
import { VehicleAttribute } from 'src/app/models/vehicle/vehicle-attribute';
import { RestResponse } from 'src/app/models/general/rest-response';

@Injectable({
   providedIn: 'root'
})
export class VehicleTypeAttributeValueService {
  private readonly endpoint = 'vehicle-attribute-value';
  constructor(
      private readonly httpClient: HttpClient
  ) {

  }

  public save(atributes: VehicleAttributeValueCreateUpdateForm):
                Observable<RestResponse<VehicleAttributeValueCreateUpdateForm>> {

    return this.httpClient.post<RestResponse<VehicleAttributeValueCreateUpdateForm>>(
                                `${environment.url}/${this.endpoint}/`, atributes);
  }

  public delete(uuid: string): Observable<RestResponse<VehicleAttributeForm>> {

    return this.httpClient
        .delete<RestResponse<VehicleAttributeForm>>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
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
