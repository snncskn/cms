import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {environment} from '../../../environments/environment';
import {catchError} from 'rxjs/operators';
import { RestResponse } from 'src/app/models/general/rest-response';
import { ItemTypeDto } from 'src/app/models/map/ItemTypeDto';
import { Pagination } from 'src/app/models/pagination-generic';
import { Item } from 'src/app/models/item/item-info';

@Injectable({
  providedIn: 'root'
})
export class ItemTypeService {
  private readonly endpoint = 'item-type';
  constructor(
      private readonly httpClient: HttpClient
  ) { }

  public save(item: Item): Observable<Item> {

    return this.httpClient.post<Item>(`${environment.url}/${this.endpoint}/`, item);
  }

  public image(url: string, image: FormData): Observable<FormData> {

    return this.httpClient.post<FormData>(url, image);
  }

  public delete(uuid: string): Observable<Item> {

    return this.httpClient.delete<Item>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
  }

  public find(uuid: string): Observable<RestResponse<Item>> {

    return this.httpClient.get<RestResponse<Item>>(`${environment.url}/${this.endpoint}/${uuid}`);
  }

  public get(path: string, params: HttpParams = new HttpParams()): Observable<any> {

    return this.httpClient.get(`${environment.url}${path}`, { params })
        .pipe(catchError(this.formatErrors));
  }

  public put(path: string, body: Object = {}): Observable<any> {

    return this.httpClient.put(
        `${environment.url}${path}`,
        JSON.stringify(body)
    ).pipe(catchError(this.formatErrors));
  }

  public post(path: string, body: Object = {} ): Observable<any> {

    return this.httpClient.post(
        `${environment.url}${path}`,
        JSON.stringify(body)
    ).pipe(catchError(this.formatErrors));
  }

  public list(): Observable<Pagination<ItemTypeDto[]>> {

    return this.httpClient
        .get<Pagination<ItemTypeDto[]>>(`${environment.url}/${this.endpoint}`);
  }
  private readonly formatErrors: (error: any) => Observable<never> = (error: any) => {
    return throwError(error.error);
  }
}
