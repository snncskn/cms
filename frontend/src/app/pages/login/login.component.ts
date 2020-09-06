import { Component, OnInit, ElementRef, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/services/general/authentication.service';
import { first } from 'rxjs/operators';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ToastrManager } from 'ng6-toastr-notifications';
import { User } from 'src/app/models/general/user';
import { UserForm } from 'src/app/models/users/userForm';

declare var $: any;

@Component({
    selector: 'app-login-cmp',
    templateUrl: './login.component.html'
})

export class LoginComponent implements OnInit, OnDestroy {
    public loginForm: FormGroup;
    public thisYear: Date = new Date();
    public toggleButton: any;
    public sidebarVisible: boolean;
    public returnUrl: string;
    public errorUrl: string;
    public  loading = false;
    public operationLock: boolean;
    private readonly nativeElement: Node;
    constructor(private readonly element: ElementRef,
                private readonly authenticationService: AuthenticationService,
                private readonly formBuilder: FormBuilder,
                public toastr: ToastrManager,
                private readonly router: Router) {
        this.nativeElement = element.nativeElement;
        this.sidebarVisible = false;
        this.returnUrl = 'dashboard';
        this.errorUrl = 'pages/login';
    }

    public ngOnInit(): void {
        this.loginForm = this.formBuilder.group({
            username: [null, Validators.required],
            password: [null, Validators.required]
        });
        this.operationLock = false;
        const navbar: HTMLElement = this.element.nativeElement;
        this.toggleButton = navbar.getElementsByClassName('navbar-toggle')[0];
        const body = document.getElementsByTagName('body')[0];
        body.classList.add('login-page');
        body.classList.add('off-canvas-sidebar');
        const card = document.getElementsByClassName('card')[0];
        setTimeout(() => {
            card.classList.remove('card-hidden');
        }, 700);
    }
    public sidebarToggle(): void {
        const toggleButton = this.toggleButton;
        const body = document.getElementsByTagName('body')[0];
        const sidebar = document.getElementsByClassName('navbar-collapse')[0];
        if (this.sidebarVisible === false) {
            setTimeout(() => {
                toggleButton.classList.add('toggled');
            }, 500);
            body.classList.add('nav-open');
            this.sidebarVisible = true;
        } else {
            this.toggleButton.classList.remove('toggled');
            this.sidebarVisible = false;
            body.classList.remove('nav-open');
        }
    }
    public ngOnDestroy(): void {
      const body = document.getElementsByTagName('body')[0];
      body.classList.remove('login-page');
      body.classList.remove('off-canvas-sidebar');
    }

    public onSubmit(): void {
        this.operationLock = true;
        const user = new UserForm;
        user.email = this.f().username.value;
        user.password = this.f().password.value;
        this.authenticationService.login(user)
            .subscribe(
                data => {
                    this.operationLock = false;
                    if (data.type === 'ERROR') {
                        this.toastr.errorToastr(data.text, 'Error!');
                        this.router.navigate([this.errorUrl]);
                    } else if (data.type === 'SUCCESS') {
                        this.toastr.successToastr(data.text, 'Succes!');
                        if (data.data.currentSite === null) {
                            data.data.siteInfos.forEach(si => {
                                data.data.currentSite = si;
                            });
                        }
                        localStorage.setItem('currentUser', JSON.stringify(data.data));
                        window.location.href = '/dashboard';

                    } else {
                        this.router.navigate([this.errorUrl]);
                    }

                },
                error => {
                    this.operationLock = false;
                });


    }

    public f = () => {
        return this.loginForm.controls;
    }

    public keyDownFunction = (event) => {
        if (event.keyCode === 13) {
         this.onSubmit();
        }
      }

}
