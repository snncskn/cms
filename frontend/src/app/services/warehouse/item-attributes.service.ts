import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {environment} from '../../../environments/environment';
import {ItemAttributesForm} from '../../models/ItemAttributesForm';
import { ItemAttributeCreateUpdateForm } from 'src/app/models/item/item-attribute';
import { Pagination } from 'src/app/models/pagination-generic';
import { RestResponse } from 'src/app/models/general/rest-response';
import { ItemAttributeT } from 'src/app/models/item/item-attribute-dto';

@Injectable({
  providedIn: 'root'
})
export class ItemAttributesService {
  private readonly endpoint = 'item-attribute';

  constructor(
      private readonly httpClient: HttpClient
  ) {

  }

  public save(attributes: ItemAttributeCreateUpdateForm): Observable<RestResponse<ItemAttributesForm>> {

    return this.httpClient.post<RestResponse<ItemAttributesForm>>(`${environment.url}/${this.endpoint}/`, attributes);
  }

  public delete(uuid: string): Observable<ItemAttributesForm> {

    return this.httpClient.delete<ItemAttributesForm>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
  }

  public find(uuid: string): Observable<ItemAttributesForm> {

    return this.httpClient.get<ItemAttributesForm>(`${environment.url}/${this.endpoint}/${uuid}`);
  }
  public list(): Observable<Pagination<ItemAttributeT[]>> {

    return this.httpClient
        .get<Pagination<ItemAttributeT[]>>(`${environment.url}/${this.endpoint}/`);
  }
  /*
  public list(): Observable<Pagination<ItemAttributeT>> {

    return this.httpClient
        .get<Pagination>(`${environment.url}/${this.endpoint}/`);
  }
*/
  private readonly formatErrors = (error: any) => {

    return throwError(error.error);
  }
}
