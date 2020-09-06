import {Injectable}             from '@angular/core';
import {environment}            from '../../../environments/environment';
import {HttpClient}             from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {Pageable, Pagination}   from 'src/app/models/pagination-generic';
import {UserInfo}               from '../../models/users/user-info';
import {UserForm}               from '../../models/users/userForm';
import {RestResponse}           from '../../models/general/rest-response';
import { UserRole } from 'src/app/models/users/user-role';

@Injectable({
    providedIn: 'root'
})
export class RoleService {
    private readonly endpoint      = 'role';

    constructor(
        private readonly httpClient: HttpClient
    ) {

    }

    public save(user: UserInfo): Observable<RestResponse<UserRole>> {

        return this.httpClient.post<RestResponse<UserRole>>(`${environment.url}/${this.endpoint}/`, user);
    }
    public update(property: UserInfo): Observable<RestResponse<UserRole>> {

        return this.httpClient.put<RestResponse<UserRole>>
        (`${environment.url}/${this.endpoint}?uuid=${property.uuid}`, property);
    }

    public delete(uuid: string): Observable<RestResponse<UserRole>> {

        return this.httpClient.delete<RestResponse<UserRole>>(`${environment.url}/${this.endpoint}?uuid=${uuid}`);
    }

    public find(uuid: string): Observable<RestResponse<UserRole>> {

        return this.httpClient.get<RestResponse<UserRole>>(`${environment.url}/${this.endpoint}/${uuid}`);
    }

    public list(page: Pageable): Observable<Pagination<UserRole[]>> {

        return this.httpClient
            .get<Pagination<UserRole[]>>(`${environment.url}/${this.endpoint}` +
                                        `?page=${page.page}&size=${page.size}`);
    }
    public filter(page: Pageable, filter: string): Observable<Pagination<UserRole[]>> {

        return this.httpClient
            .get<Pagination<UserRole[]>>
                (`${environment.url}/${this.endpoint}/filter/${filter}?page=${page.page}&size=${page.size}`);
    }

    private readonly formatErrors: (error: any) => Observable<never> = (error: any) => {
        return throwError(error.error);
    }
}
