import { environment } from '../../../environments/environment';
import { HttpClient} from '@angular/common/http';
import { Observable, throwError} from 'rxjs';
import { Injectable } from '@angular/core';
import { Pageable, Pagination } from 'src/app/models/pagination-generic';
import { RestResponse } from 'src/app/models/general/rest-response';
import {PropertyCreateUpdateForm} from '../../models/property/property-create-update-form';
import {PropertyForm} from '../../models/property/property-form';
import {Property} from '../../models/property/property';
@Injectable({
    providedIn: 'root'
})
export class PropertyService {

    private readonly endpoint = 'property';

    constructor(
        private readonly httpClient: HttpClient
    ) {

    }
    public save(property: Property): Observable<RestResponse<PropertyForm>> {

        return this.httpClient.post<RestResponse<PropertyForm>>(`${environment.url}/${this.endpoint}/`, property);
    }

    public delete(uuid: string): Observable<PropertyForm> {

        return this.httpClient.delete<PropertyForm>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
    }

    public update(uuid: string, property: Property): Observable<PropertyCreateUpdateForm> {

        return this.httpClient.put<PropertyCreateUpdateForm>
        (`${environment.url}/${this.endpoint}?uuid=${uuid}`, property);
    }
    public find(uuid: string): Observable<RestResponse<PropertyForm>> {

        return this.httpClient.get<RestResponse<PropertyForm>>(`${environment.url}/${this.endpoint}/${uuid}`);
    }

    public list(page: Pageable): Observable<Pagination<PropertyForm[]>> {

        return this.httpClient
            .get<Pagination<PropertyForm[]>>(
                `${environment.url}/${this.endpoint}?page=${page.page}&size=${page.size}`);
    }
    public filter(page: Pageable, filter: string): Observable<Pagination<PropertyForm[]>> {
        return this.httpClient
            .get<Pagination<PropertyForm[]>>
                (`${environment.url}/${this.endpoint}/filter/${filter}?page=${page.page}&size=${page.size}`);
    }
    public findByGroupName(page: Pageable, filter: string): Observable<Pagination<PropertyForm[]>> {
        return this.httpClient
            .get<Pagination<PropertyForm[]>>
                (`${environment.url}/${this.endpoint}/findByGroupName/${filter}?page=${page.page}&size=${page.size}`);
    }
    private readonly formatErrors: (error: any) => Observable<never> = (error: any) => {
        return throwError(error.error);
    }
}
