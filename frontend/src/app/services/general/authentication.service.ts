import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { User } from 'src/app/models/general/user';
import { environment } from 'src/environments/environment';
import { RestResponse } from 'src/app/models/general/rest-response';
import { UserForm } from 'src/app/models/users/userForm';

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService {
    public currentUser: Observable<User>;
    private readonly currentUserSubject: BehaviorSubject<User>;
    private readonly endpoint = 'login';
    constructor(private readonly http: HttpClient) {
        try {
            this.currentUserSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('currentUser')));
            this.currentUser = this.currentUserSubject.asObservable();
        } catch (error) {
        }
    }

    public get currentUserValue(): User {
        return  this.currentUserSubject.getValue();
    }

    public login = (user: UserForm) => {
        return this.http.post<RestResponse<User>>(`${environment.url}/${this.endpoint}`, user);
    }

    public logout = () => {
        localStorage.removeItem('currentUser');
        this.currentUserSubject.next(null);
    }
}
