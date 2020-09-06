import { environment } from '../../../environments/environment';
import { HttpClient} from '@angular/common/http';
import { Observable, throwError} from 'rxjs';
import { Injectable } from '@angular/core';
import { Pageable, Pagination } from 'src/app/models/pagination-generic';
import { RestResponse } from 'src/app/models/general/rest-response';
import {SiteForm} from '../../models/sites/site-form';
import {RoleForm} from '../../models/roles/role-form';
import {RoleCreateUpdateForm} from '../../models/roles/role-create-update-form';
import {Property} from '../../models/property/property';
import {PropertyCreateUpdateForm} from '../../models/property/property-create-update-form';

@Injectable({
    providedIn: 'root'
})
export class RoleService {

    private readonly endpoint = 'role';

    constructor(
        private readonly httpClient: HttpClient
    ) {

    }
    public save(role: RoleCreateUpdateForm): Observable<RestResponse<RoleForm>> {

        return this.httpClient.post<RestResponse<RoleForm>>(`${environment.url}/${this.endpoint}/`, role);
    }

    public delete(uuid: string): Observable<RoleForm> {

        return this.httpClient.delete<RoleForm>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
    }

    public find(uuid: string): Observable<RestResponse<RoleForm>> {

        return this.httpClient.get<RestResponse<RoleForm>>(`${environment.url}/${this.endpoint}/${uuid}`);
    }

    public update(uuid: string, roleForm: RoleForm): Observable<RoleCreateUpdateForm> {

        return this.httpClient.put<RoleCreateUpdateForm>
        (`${environment.url}/${this.endpoint}?uuid=${uuid}`, roleForm);
    }
    public list(page: Pageable): Observable<Pagination<RoleForm[]>> {

        return this.httpClient
            .get<Pagination<RoleForm[]>>(
                `${environment.url}/${this.endpoint}?page=${page.page}&size=${page.size}`);
    }
    public filter(page: Pageable, filter: string): Observable<Pagination<RoleForm[]>> {
        return this.httpClient
            .get<Pagination<RoleForm[]>>
            (`${environment.url}/${this.endpoint}/filter/${filter}?page=${page.page}&size=${page.size}`);
    }
    private readonly formatErrors: (error: any) => Observable<never> = (error: any) => {
        return throwError(error.error);
    }
}
