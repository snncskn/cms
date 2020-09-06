import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { AuthenticationService } from 'src/app/services/general/authentication.service';
import { Observable } from 'rxjs';



@Injectable()
export class JwtInterceptor implements HttpInterceptor {
    constructor(private readonly authenticationService: AuthenticationService) {}

    public intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const currentUser = this.authenticationService.currentUserValue;
        const prm = (request.urlWithParams.indexOf('xls') > -1) ? 'responseType: \'blob\'' : '';

        if (currentUser && currentUser.token) {
            if (currentUser.currentSite !== null) {
                request = request.clone({
                    setHeaders: {
                        Authorization: `Bearer ${currentUser.token}`,
                        Site: ` ${currentUser.currentSite.uuid}`,
                        prm
                    }
                });
            } else {
                request = request.clone({
                    setHeaders: {
                        Authorization: `Bearer ${currentUser.token}`
                    }
                });
            }
        }

        return next.handle(request);
    }
}
