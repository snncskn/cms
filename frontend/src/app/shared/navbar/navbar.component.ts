import { Component, OnInit, Renderer, ViewChild, ElementRef, Directive } from '@angular/core';
import { ROUTES } from '../.././sidebar/sidebar.component';
import { Router, ActivatedRoute, NavigationEnd, NavigationStart } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { Location, LocationStrategy, PathLocationStrategy } from '@angular/common';
import { AuthenticationService } from 'src/app/services/general/authentication.service';
import { FormGroup, FormBuilder } from '@angular/forms';
import { SiteService } from 'src/app/services/general/site.service';
import { Pageable } from 'src/app/models/map/pagination-attribute';
import { MatTableDataSource } from '@angular/material';
import { SiteForm } from 'src/app/models/sites/site-form';
import { UserService } from 'src/app/services/general/user.service';
import { UserSiteUpdate } from 'src/app/models/users/user-site-update';
import { User } from 'src/app/models/general/user';
const misc: any = {
    navbar_menu_visible: 0,
    active_collapse: true,
    disabled_collapse_init: 0,
};

declare var $: any;

@Component({
    selector: 'app-navbar-cmp',
    templateUrl: 'navbar.component.html'
})

export class NavbarComponent implements OnInit {
    public form: FormGroup;
    public page: Pageable;
    public userSiteUpdate: UserSiteUpdate;
    public user: User;

    private listTitles: any[];
    private readonly location: Location;
    private mobile_menu_visible: any = 0;
    private readonly nativeElement: Node;
    private toggleButton: any;
    private sidebarVisible: boolean;
    private _router: Subscription;




    @ViewChild('app-navbar-cmp') private readonly button: any;

    constructor(location: Location, private readonly renderer: Renderer,
        private readonly element: ElementRef,
        private readonly formBuilder: FormBuilder,
        private readonly authenticationService: AuthenticationService,
        private readonly userService: UserService,
        private readonly router: Router) {
        this.location = location;
        this.nativeElement = element.nativeElement;
        this.sidebarVisible = false;
        this.form = this.formBuilder.group({
            search: [null],
            site: [0],
        });
        this.userSiteUpdate = new UserSiteUpdate();
        this.user = this.authenticationService.currentUserValue;
    }
    public minimizeSidebar(): void {
        const body = document.getElementsByTagName('body')[0];

        if (misc.sidebar_mini_active === true) {
            body.classList.remove('sidebar-mini');
            misc.sidebar_mini_active = false;

        } else {
            setTimeout(() => {
                body.classList.add('sidebar-mini');

                misc.sidebar_mini_active = true;
            }, 300);
        }

        // we simulate the window Resize so the charts will get updated in realtime.
        const simulateWindowResize = setInterval(() => {
            window.dispatchEvent(new Event('resize'));
        }, 180);

        // we stop the simulation of Window Resize after the animations are completed
        setTimeout(() => clearInterval(simulateWindowResize), 1000);

    }
    public hideSidebar(): void {
        const body = document.getElementsByTagName('body')[0];
        const sidebar = document.getElementsByClassName('sidebar')[0];

        if (misc.hide_sidebar_active === true) {
            setTimeout(() => {
                body.classList.remove('hide-sidebar');
                misc.hide_sidebar_active = false;
            }, 300);

            setTimeout(() => {
                sidebar.classList.remove('animation');
            }, 600);
            sidebar.classList.add('animation');

        } else {
            setTimeout(() => {
                body.classList.add('hide-sidebar');
                misc.hide_sidebar_active = true;
            }, 300);
        }


        const simulateWindowResize = setInterval(() => {
            window.dispatchEvent(new Event('resize'));
        }, 180);

        setTimeout(() => clearInterval(simulateWindowResize), 1000);
    }

    public ngOnInit(): void {
        this.listTitles = ROUTES.filter(listTitle => listTitle);

        const navbar: HTMLElement = this.element.nativeElement;
        const body = document.getElementsByTagName('body')[0];
        this.toggleButton = navbar.getElementsByClassName('navbar-toggler')[0];
        if (body.classList.contains('sidebar-mini')) {
            misc.sidebar_mini_active = true;
        }
        if (body.classList.contains('hide-sidebar')) {
            misc.hide_sidebar_active = true;
        }
        this._router = this.router.events.filter(
            event => event instanceof NavigationEnd).subscribe(
                (event: NavigationEnd) => {
                    this.sidebarClose();

                    const $layer = document.getElementsByClassName('close-layer')[0];
                    if ($layer) {
                        $layer.remove();
                    }
                });
    }
    public onResize = (event) => {
        if ($(window).width() > 991) {
            return false;
        }
        return true;
    }
    public sidebarOpen(): void {
        const $toggle = document.getElementsByClassName('navbar-toggler')[0];
        const toggleButton = this.toggleButton;
        const body = document.getElementsByTagName('body')[0];
        setTimeout(() => {
            toggleButton.classList.add('toggled');
        }, 500);
        body.classList.add('nav-open');
        setTimeout(() => {
            $toggle.classList.add('toggled');
        }, 430);

        const $layer = document.createElement('div');
        $layer.setAttribute('class', 'close-layer');


        if (body.querySelectorAll('.main-panel')) {
            document.getElementsByClassName('main-panel')[0].appendChild($layer);
        } else if (body.classList.contains('off-canvas-sidebar')) {
            document.getElementsByClassName('wrapper-full-page')[0].appendChild($layer);
        }

        setTimeout(() => $layer.classList.add('visible'), 100);

        $layer.onclick = (() => {
            body.classList.remove('nav-open');
            this.mobile_menu_visible = 0;
            this.sidebarVisible = false;

            $layer.classList.remove('visible');
            setTimeout(() => {
                $layer.remove();
                $toggle.classList.remove('toggled');
            }, 400);
        }).bind(this);

        body.classList.add('nav-open');
        this.mobile_menu_visible = 1;
        this.sidebarVisible = true;
    }
    public sidebarClose(): void {
        const $toggle = document.getElementsByClassName('navbar-toggler')[0];
        const body = document.getElementsByTagName('body')[0];
        this.toggleButton.classList.remove('toggled');
        const $layer = document.createElement('div');
        $layer.setAttribute('class', 'close-layer');

        this.sidebarVisible = false;
        body.classList.remove('nav-open');
        // $('html').removeClass('nav-open');
        body.classList.remove('nav-open');
        if ($layer) {
            $layer.remove();
        }
        setTimeout(() => $toggle.classList.remove('toggled'), 400);
        this.mobile_menu_visible = 0;
    }
    public sidebarToggle(): void {
        if (this.sidebarVisible === false) {
            this.sidebarOpen();
        } else {
            this.sidebarClose();
        }
    }

    public getTitle(): string {
        const titlee: any = this.location.prepareExternalUrl(this.location.path());
        for (let i = 0; i < this.listTitles.length; i++) {
            if (this.listTitles[i].type === 'link' && this.listTitles[i].path === titlee) {
                return this.listTitles[i].title;
            } else if (this.listTitles[i].type === 'sub') {
                for (let j = 0; j < this.listTitles[i].children.length; j++) {
                    const subtitle = this.listTitles[i].path + '/' + this.listTitles[i].children[j].path;
                    if (subtitle === titlee) {
                        return this.listTitles[i].children[j].title;
                    }
                }
            }
        }
        return 'Dashboard';
    }
    public getPath = () => {
        return this.location.prepareExternalUrl(this.location.path());
    }
    public logout(): void {
        this.authenticationService.logout();
        this.router.navigateByUrl('/pages/login');
    }
    public selectedSite(item: SiteForm): void {
        this.user = this.authenticationService.currentUserValue;
        this.user.currentSite = item;
        this.authenticationService.currentUserValue.siteInfos.forEach(ste => {
            if (ste.supplierUuid === item.supplierUuid) {
                this.user.currentSite.supplierInfo = ste.supplierInfo;

            }

        });
        localStorage.setItem('currentUser', JSON.stringify(this.user));
        this.router.navigateByUrl(this.router.url);
        window.location.reload(false);

    }
    public onClickProfile = () => {
        this.router.navigateByUrl('/profile');

    }
}
