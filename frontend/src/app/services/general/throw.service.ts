import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable ,  throwError } from 'rxjs';
import { Pageable, Pagination } from 'src/app/models/pagination-generic';
import { RestResponse } from 'src/app/models/general/rest-response';
import { UserInfo} from '../../models/users/user-info';
import { Throw } from 'src/app/models/general/throw';

@Injectable({
    providedIn: 'root'
})
export class ThrowService {
    private readonly endpoint = 'throw';
    constructor(
        private readonly httpClient: HttpClient
) {

    }
    public save(throwEntity: Throw): Observable<Throw> {
        return this.httpClient.post<Throw>(`${environment.url}/${this.endpoint}/`, throwEntity);
    }
    public list(page: Pageable): Observable<Pagination<Throw[]>> {

        return this.httpClient
            .get<Pagination<Throw[]>>(`${environment.url}/${this.endpoint}?page=${page.page}&size=${page.size}`);
    }
    private readonly formatErrors: (error: any) => Observable<never> = (error: any) => {
        return throwError(error.error);
    }
}
