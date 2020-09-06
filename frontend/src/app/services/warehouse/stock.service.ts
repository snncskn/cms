import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {environment} from '../../../environments/environment';
import {catchError} from 'rxjs/operators';
import { RestResponse } from 'src/app/models/general/rest-response';
import { Pagination } from 'src/app/models/pagination-generic';
import { StockFilterForm } from 'src/app/models/stock/stock-filter';
import { StockHistoryInfo } from 'src/app/models/stock/stock-report';
import { AuthenticationService } from '../general/authentication.service';
@Injectable({
  providedIn: 'root'
})
export class StockService {
  private readonly endpoint = 'stock';
  constructor(
      private readonly httpClient: HttpClient,
      private readonly authenticationService: AuthenticationService) { }

  public findAllStock(filter: StockFilterForm): Observable<Pagination<StockHistoryInfo[]>> {
    return this.httpClient.post<Pagination<StockHistoryInfo[]>>
      (`${environment.url}/${this.endpoint}/find-all-stock/`, filter);
  }

  public excel(filter: StockFilterForm): Observable<Blob> {
    filter.size = 99999;
    filter.page = 0;
    const currentUser = this.authenticationService.currentUserValue;
    return this.httpClient.post(`${environment.url}/${this.endpoint}/xls`, filter, {
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
