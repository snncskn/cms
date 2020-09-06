import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RestResponse } from 'src/app/models/general/rest-response';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';
import { BreakDown } from 'src/app/models/workshop/break-down/break-down';
import { BreakDownImage } from 'src/app/models/workshop/break-down/break-down-image';
import { Workshop } from 'src/app/models/workshop/workshop';
import { Pageable, Pagination } from 'src/app/models/pagination-generic';
import { BreakDownSearch } from 'src/app/models/workshop/break-down/break-down-search';
import { JobCardCreateUpdateForm } from 'src/app/models/workshop/job-card/create-job-card';
import { JobCardHistoryCreateForm } from 'src/app/models/workshop/break-down/job-create-desc';
import { JobCardItemCreateForm } from 'src/app/models/workshop/break-down/job-create-item';
import { JobCardInfo } from 'src/app/models/workshop/job-card/job-card-info';
import { JobCardInfoItem } from 'src/app/models/workshop/job-card/job-card-info-item';
import { JobCardUpdateForm } from 'src/app/models/workshop/job-card/job-update';
import { RequestInfo } from 'src/app/models/request/request-info';
import { RequestListFilterForm } from 'src/app/models/request/request-filter';
import { ItemCreateRequest } from 'src/app/models/request/item-create-request';
import { JobCardItemDeliveredListInfo } from 'src/app/models/request/item-delivered-info';
import { AuthenticationService } from '../general/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class BreakDownService {
  private readonly endpoint = 'job-card';

  constructor(private readonly httpClient: HttpClient,
              private readonly authenticationService: AuthenticationService) {
  }
  public createBreakDown(item: BreakDown): Observable<RestResponse<BreakDown>> {
    return this.httpClient.post<RestResponse<BreakDown>>
      (`${environment.url}/${this.endpoint}/create-break-down`, item);
  }
  public createJobCardHistory(item: JobCardHistoryCreateForm): Observable<boolean> {
    return this.httpClient.post<boolean>
      (`${environment.url}/${this.endpoint}/create-job-card-history`, item);
  }
  public createJobCard(item: JobCardCreateUpdateForm): Observable<RestResponse<BreakDown>> {
    return this.httpClient.post<RestResponse<BreakDown>>
      (`${environment.url}/${this.endpoint}/create-job-card`, item);
  }
  public findJobCardHistory(jobCardUuid: string, page: Pageable): Observable<Pagination<JobCardHistoryCreateForm[]>> {
    return this.httpClient.get<Pagination<JobCardHistoryCreateForm[]>>
      (`${environment.url}/${this.endpoint}/find-job-card-history/${jobCardUuid}?page=${page.page}&size=${page.size}`);
  }
  public findJobCard(jobCardUuid: string): Observable<RestResponse<JobCardInfo>> {
    return this.httpClient.get<RestResponse<JobCardInfo>>
      (`${environment.url}/${this.endpoint}/find-job-card/${jobCardUuid}`);
  }
  public findJobCardItem(jobCardUuid: string, page: Pageable): Observable<Pagination<JobCardInfoItem[]>> {
    return this.httpClient.get<Pagination<JobCardInfoItem[]>>
      (`${environment.url}/${this.endpoint}/find-job-card-item/${jobCardUuid}?page=${page.page}&size=${page.size}`);
  }
  public createJobCardItem(jobCardItemCreateForm: JobCardItemCreateForm):
    Observable<boolean> {
    return this.httpClient.post<boolean>
      (`${environment.url}/${this.endpoint}/create-job-card-item`, jobCardItemCreateForm);
  }
  public jobCardUpdateForm(jobCardUpdateForm: JobCardUpdateForm):
    Observable<boolean> {
    return this.httpClient.put<boolean>
      (`${environment.url}/${this.endpoint}/update-job-card/` +
        `${jobCardUpdateForm.jobCardUuid}`, jobCardUpdateForm);
  }
  public allBreakDown(breakDownSearch: BreakDownSearch): Observable<Pagination<Workshop[]>> {
    return this.httpClient.post<Pagination<Workshop[]>>
      (`${environment.url}/${this.endpoint}/list-all-break-down/`, breakDownSearch);
  }
  public image(breakDownImage: BreakDownImage): Observable<RestResponse<BreakDown>> {
    return this.httpClient.post<RestResponse<BreakDown>>
      (`${environment.url}/${this.endpoint}/create-image-job-card`, breakDownImage);
  }

  public find(uuid: string): Observable<RestResponse<Workshop>> {

    return this.httpClient.get<RestResponse<Workshop>>(`${environment.url}/${this.endpoint}/${uuid}`);
  }

  public closeJobCard(jobCardUuid: string): Observable<RestResponse<JobCardInfo>> {
    return this.httpClient.put<RestResponse<JobCardInfo>>
      (`${environment.url}/${this.endpoint}/close-job-card/${jobCardUuid}`, null);
  }
  public listAllRequestList(filter: RequestListFilterForm): Observable<Pagination<RequestInfo[]>> {
    return this.httpClient.post<Pagination<RequestInfo[]>>
      (`${environment.url}/${this.endpoint}/list-all-request-list/`, filter);
  }

  public xlsRequest(filter: RequestListFilterForm): Observable<Blob> {
    filter.size = 99999;
    filter.page = 0;
    const currentUser = this.authenticationService.currentUserValue;
    return this.httpClient.post(`${environment.url}/${this.endpoint}/xlsRequest`, filter, {
      headers: {
        Authorization: `Bearer ${currentUser.token}`,
        Site: ` ${currentUser.currentSite.uuid}`
      },
      responseType: 'blob'
    });
  }
  public xls(filter: BreakDownSearch): Observable<Blob> {
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
  public createJobCardItemRequest(itemCreateRequest: ItemCreateRequest): Observable<RestResponse<JobCardInfo>> {
    return this.httpClient.post<RestResponse<JobCardInfo>>
      (`${environment.url}/${this.endpoint}/create-job-card-item-request`, itemCreateRequest);
  }
  public deleteJobCardItem(uuid: string): Observable<RestResponse<boolean>> {

    return this.httpClient.delete<RestResponse<boolean>>(`
            ${environment.url}/${this.endpoint}/delete-job-card-item?jobCardItemUuid=${uuid}`);
  }


  public findJobCardItemDelivered(jobCardItemUuid: string, page: Pageable):
                                    Observable<Pagination<JobCardItemDeliveredListInfo[]>> {
    return this.httpClient.get<Pagination<JobCardItemDeliveredListInfo[]>>
      (`${environment.url}/${this.endpoint}/find-job-card-item-delivered/` +
                              `${jobCardItemUuid}?page=${page.page}&size=${page.size}`);
  }

}
