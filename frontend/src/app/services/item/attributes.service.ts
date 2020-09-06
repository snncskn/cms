import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {environment} from '../../../environments/environment';
import {Injectable} from '@angular/core';
import {VehicleTypeCreateUpdateForm} from '../../models/vehicle/vehicle-type-create-update-form';
import { VehicleAttributeCreateUpdateForm } from 'src/app/models/map/vehicle-attribute-create-update-form';
import { Pageable, Pagination } from 'src/app/models/pagination-generic';
import { VehicleAttribute } from 'src/app/models/vehicle/vehicle-attribute';
import { VehicleTypeAttributeDto } from 'src/app/models/map/VehicleTypeAttributeDto';

@Injectable({
  providedIn: 'root'
})
export class ItemAttributesService {

  private readonly endpoint = 'item-attribute';

  constructor(
      private readonly httpClient: HttpClient
  ) {

  }


  public save(atributes: VehicleAttributeCreateUpdateForm): Observable<VehicleAttributeCreateUpdateForm> {

    return this.httpClient.post<VehicleAttributeCreateUpdateForm>
                    (`${environment.url}/${this.endpoint}`, atributes);
  }

  public update(atributes: VehicleAttributeCreateUpdateForm): Observable<VehicleAttributeCreateUpdateForm> {

    return this.httpClient.put<VehicleAttributeCreateUpdateForm>
                    (`${environment.url}/${this.endpoint}?uuid=${atributes.uuid}`, atributes);
  }

  public delete(uuid: string): Observable<VehicleTypeCreateUpdateForm> {

    return this.httpClient.delete<VehicleTypeCreateUpdateForm>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
  }

  public find(uuid: string): Observable<VehicleAttributeCreateUpdateForm> {

    return this.httpClient.get<VehicleAttributeCreateUpdateForm>(`${environment.url}/${this.endpoint}/${uuid}`);
  }

  public list(page: Pageable): Observable<Pagination<VehicleAttribute[]>> {

    return this.httpClient
        .get<Pagination<VehicleAttribute[]>>(`${environment.url}/${this.endpoint}?page=${page.page}&size=${page.size}`);
  }
  public filter(page: Pageable, filter: string): Observable<Pagination<VehicleAttribute[]>> {
    return this.httpClient
        .get<Pagination<VehicleAttribute[]>>
            (`${environment.url}/${this.endpoint}/filter/${filter}?page=${page.page}&size=${page.size}`);
}


  public listWithValue(page: Pageable): Observable<Pagination<VehicleTypeAttributeDto[]>> {

    return this.httpClient
        .get<Pagination<VehicleTypeAttributeDto[]>>(
                          `${environment.url}/${this.endpoint}?page=${page.page}&size=${page.size}`);
  }
  private readonly formatErrors = (error: any) => {

    return throwError(error.error);
  }

}
