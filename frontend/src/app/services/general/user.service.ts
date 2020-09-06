import {Injectable}             from '@angular/core';
import {environment}            from '../../../environments/environment';
import {HttpClient}             from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {Pageable, Pagination}   from 'src/app/models/pagination-generic';
import {UserInfo}               from '../../models/users/user-info';
import {UserForm}               from '../../models/users/userForm';
import {RestResponse}           from '../../models/general/rest-response';
import { UserSiteUpdate } from 'src/app/models/users/user-site-update';
import { SiteForm } from 'src/app/models/sites/site-form';

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private readonly endpoint      = 'user';
    private readonly imageEndpoint = 'image';

    constructor(
        private readonly httpClient: HttpClient
    ) {

    }

    public save(user: UserInfo): Observable<RestResponse<UserForm>> {

        return this.httpClient.post<RestResponse<UserForm>>(`${environment.url}/${this.endpoint}/`, user);
    }
    public update(property: UserInfo): Observable<RestResponse<UserForm>> {

        return this.httpClient.put<RestResponse<UserForm>>
        (`${environment.url}/${this.endpoint}?uuid=${property.uuid}`, property);
    }
    public image(file: FormData): Observable<FormData> {
        return this.httpClient.post<FormData>(`${environment.url}/${this.imageEndpoint}`, file);
    }

    public delete(uuid: string): Observable<RestResponse<UserForm>> {

        return this.httpClient.delete<RestResponse<UserForm>>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
    }

    public find(uuid: string): Observable<RestResponse<UserForm>> {

        return this.httpClient.get<RestResponse<UserForm>>(`${environment.url}/${this.endpoint}/${uuid}`);
    }

    public list(page: Pageable): Observable<Pagination<UserForm[]>> {

        return this.httpClient
            .get<Pagination<UserForm[]>>(`${environment.url}/${this.endpoint}?page=${page.page}&size=${page.size}`);
    }

    public userGroup(page: Pageable, filter: string): Observable<Pagination<UserForm[]>> {

        return this.httpClient
            .get<Pagination<UserForm[]>>
                (`${environment.url}/${this.endpoint}/find-position/${filter}?page=${page.page}&size=${page.size}`);
    }

    public filter(page: Pageable, filter: string): Observable<Pagination<UserForm[]>> {

        return this.httpClient
            .get<Pagination<UserForm[]>>
                (`${environment.url}/${this.endpoint}/filter/${filter}?page=${page.page}&size=${page.size}`);
    }

   public updateSite(userSite: UserSiteUpdate): Observable<RestResponse<SiteForm>> {
        return this.httpClient.post<RestResponse<SiteForm>>(`
        ${environment.url}/${this.endpoint}/update-site`, userSite);

    }
    private readonly formatErrors: (error: any) => Observable<never> = (error: any) => {
        return throwError(error.error);
    }
}
