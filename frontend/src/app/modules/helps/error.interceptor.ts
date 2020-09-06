import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { AuthenticationService } from 'src/app/services/general/authentication.service';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ToastrManager } from 'ng6-toastr-notifications';
import { ThrowService } from 'src/app/services/general/throw.service';
import { Throw } from 'src/app/models/general/throw';
import { Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    public data: Throw;
    public errorLog: boolean;
    public loginUrl: string;

    constructor(private readonly authenticationService: AuthenticationService,
        public toastr: ToastrManager,
        public throwService: ThrowService,
        private readonly ngxService: NgxUiLoaderService,
        private readonly router: Router
        ) {
            this.data = new Throw();
            this.loginUrl = 'pages/login';

        }

    public intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(catchError(err => {
            if (err.status === 401) {
                this.toastr.errorToastr(err.error.data, 'Error');
                this.errorLog = false;
                this.authenticationService.logout();
            } else if (err.status === 402) {
                this.toastr.errorToastr(err.error.text, err.error.type);
                this.errorLog = false;
            } else if (err.status === 406) {
                this.toastr.errorToastr(err.error.text, 'Error');
                this.errorLog = false;
            } else if (err.status === 403) {
                this.errorLog = false;
                this.authenticationService.logout();
                this.router.navigate([this.loginUrl]);
            } else if (err.status === 400) {
                this.toastr.errorToastr(err.error.text, err.error.type);
                this.errorLog = false;
            } else if (err.status === 500) {
               this.errorLog = false;
            } else if (err.status === 405) {
                this.ngxService.stop();
                this.toastr.errorToastr(err.error.text, err.error.type);
                this.errorLog = false;
                this.loginUrl = '/';
                this.authenticationService.logout();
                this.router.navigate([this.loginUrl]);
            } else if (err.status === 0) {
            }
            if (this.errorLog) {
                this.data.data = err.message;
                this.throwService.save(this.data).subscribe(data => {
                });
            }
            const error = err.error.message || err.statusText;
            return throwError(error);
        }));
    }
}
