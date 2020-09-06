import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {environment} from '../../../environments/environment';
import {Pagination} from '../../models/map/pagination';
import {ItemTypeAttributeValueForm} from '../../models/map/ItemTypeAttributeValueForm';
import { ItemAttributeValueCreateUpdateForm } from 'src/app/models/item/item-attribute-value';
import { RestResponse } from 'src/app/models/general/rest-response';

@Injectable({
  providedIn: 'root'
})
export class ItemAttributeValueService {

  private readonly endpoint = 'item-attribute-value';

  constructor(
      private readonly httpClient: HttpClient
  ) {

  }

  public create(form: ItemAttributeValueCreateUpdateForm):
                Observable<RestResponse<ItemAttributeValueCreateUpdateForm>> {

    return this.httpClient.post<RestResponse<ItemAttributeValueCreateUpdateForm>>(
                                    `${environment.url}/${this.endpoint}/`, form);
  }


  public save(item: ItemTypeAttributeValueForm): Observable<ItemTypeAttributeValueForm> {

    return this.httpClient.post<ItemTypeAttributeValueForm>(`${environment.url}/${this.endpoint}/`, item);
  }

  public delete(uuid: string): Observable<ItemTypeAttributeValueForm> {

    return this.httpClient.delete<ItemTypeAttributeValueForm>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
  }

  public find(uuid: string): Observable<ItemTypeAttributeValueForm> {

    return this.httpClient.get<ItemTypeAttributeValueForm>(`${environment.url}/${this.endpoint}/${uuid}`);
  }

  public list(): Observable<Pagination> {

    return this.httpClient
        .get<Pagination>(`${environment.url}/${this.endpoint}/`);
  }

  private readonly formatErrors = (error: any) => {

    return  throwError(error.error);
  }

}
