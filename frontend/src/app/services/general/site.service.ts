import { environment } from '../../../environments/environment';
import { HttpClient} from '@angular/common/http';
import { Observable, throwError} from 'rxjs';
import { Injectable } from '@angular/core';
import { Pageable, Pagination } from 'src/app/models/pagination-generic';
import { RestResponse } from 'src/app/models/general/rest-response';
import {SiteForm} from '../../models/sites/site-form';

@Injectable({
    providedIn: 'root'
})
export class SiteService {

    private readonly endpoint = 'site';

    constructor(
        private readonly httpClient: HttpClient
    ) {

    }
    public save(site: SiteForm): Observable<RestResponse<SiteForm>> {

        return this.httpClient.post<RestResponse<SiteForm>>(`${environment.url}/${this.endpoint}/`, site);
    }

    public delete(uuid: string): Observable<SiteForm> {

        return this.httpClient.delete<SiteForm>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
    }

    public find(uuid: string): Observable<RestResponse<SiteForm>> {

        return this.httpClient.get<RestResponse<SiteForm>>(`${environment.url}/${this.endpoint}/${uuid}`);
    }

    public list(page: Pageable): Observable<Pagination<SiteForm[]>> {

        return this.httpClient
            .get<Pagination<SiteForm[]>>(
                `${environment.url}/${this.endpoint}?page=${page.page}&size=${page.size}`);
    }
    public filter(page: Pageable, filter: string): Observable<Pagination<SiteForm[]>> {
        return this.httpClient
            .get<Pagination<SiteForm[]>>
                (`${environment.url}/${this.endpoint}/filter/${filter}?page=${page.page}&size=${page.size}`);
    }
    private readonly formatErrors: (error: any) => Observable<never> = (error: any) => {
        return throwError(error.error);
    }
}
