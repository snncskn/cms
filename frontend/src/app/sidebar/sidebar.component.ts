import {Component, OnInit} from '@angular/core';
import PerfectScrollbar from 'perfect-scrollbar';
import {Router} from '@angular/router';
import { AuthenticationService } from '../services/general/authentication.service';
import { User } from '../models/general/user';
import { Image } from '../models/general/image';
import { ImageService } from '../services/general/image.service';

declare const $: any;

export interface RouteInfo {
    id?: number;
    path: string;
    title: string;
    type: string;
    icontype: string;
    collapse?: string;
    show?: boolean;
    children?: ChildrenItems[];
}

export interface ChildrenItems {
    id?: number;
    path: string;
    title: string;
    ab: string;
    type?: string;
    show?: boolean;
}

export const ROUTES: RouteInfo[] = [
    {
        id: 1,
        path: '/vehicle/machines',
        title: 'Fleet List',
        type: 'link',
        icontype: 'local_shipping',
        show: true
    },
    {
        id: 2,
        path: '/workshop/list',
        title: 'Workshop',
        type: 'link',
        icontype: 'local_shipping',
        show: true
    },
    {
        id: 3,
        path: '/vehicle/machine',
        title: 'Enter New Machine Information',
        type: 'link',
        icontype: 'local_shipping',
        show: false
    },
    {
        id: 30,
        path: '/vehicle/machine/:id',
        title: 'Enter New Machine Information',
        type: 'link',
        icontype: 'local_shipping',
        show: false
    },
    {
        id: 4,
        path: '/warehouse',
        title: 'Warehouse',
        type: 'sub',
        icontype: 'local_shipping',
        collapse: 'warehouse',
        show: true,
        children: [
            {id: 5, path: 'items', title: 'Items', ab: 'IT', show: true},
            {id: 6, path: 'item', title: 'New Item', ab: 'NI', show: false},
            {id: 7, path: 'purchases', title: 'Purchases', ab: 'PU',  show: true},
            {id: 8, path: 'purchase', title: 'New Purchase', ab: 'NP' , show: false},
            {id: 9, path: 'suppliers', title: 'Suppliers', ab: 'SU',  show: true},
            {id: 10, path: 'supplier', title: 'New Supplier', ab: 'NS', show: false},
            {id: 11, path: 'request', title: 'Request List', ab: 'RL', show: true},
            {id: 29, path: 'transfers', title: 'Stock Transfer', ab: 'ST', show: true},
            {id: 30, path: 'stocks', title: 'Stock movements', ab: 'SL', show: true},
          ]
    },
    {
        id: 12,
        path: '/settings',
        title: 'Settings',
        type: 'sub',
        icontype: 'local_shipping',
        collapse: 'settings',
        show: true,
        children: [
            { id: 13, path: 'vehicle-types', title: 'Machine Types', ab: 'MY', show: true},
            { id: 14, path: 'vehicle-type', title: 'New Machine Type', ab: 'MY', show: false},
            { id: 15, path: 'vehicle-attributes', title: 'Machine Attributes', ab: 'MA', show: true},
            { id: 16, path: 'vehicle-attribute', title: 'New Machine Attribute', ab: 'MA', show: false},
            { id: 17, path: 'item-types', title: 'Item Type List', ab: 'IT', show: true},
            { id: 18, path: 'item-type', title: 'New Item Type', ab: 'IT', show: false},
            { id: 19, path: 'item-attributes', title: 'Item Attribute List', ab: 'IA', show: true},
            { id: 20, path: 'item-attribute', title: 'New Item Attribute', ab: 'IA', show: false},
            { id: 21, path: 'user', title: 'Add New User', ab: 'AU', show: false},
            { id: 22, path: 'users', title: 'Users List', ab: 'UL', show: true},
            { id: 23, path: 'sites', title: 'Sites List', ab: 'SL', show: true},
            { id: 24, path: 'site', title: 'New Site', ab: 'SL', show: false},
            { id: 25, path: 'propertys', title: 'Property List', ab: 'PR', show: true},
            { id: 26, path: 'property', title: 'Property', ab: 'PR', show: false},
            { id: 27, path: 'roles', title: 'Roles List', ab: 'RL', show: true},
            { id: 28, path: 'role', title: 'Role Form', ab: 'RF', show: false}
        ]
    }
];
@Component({
    selector: 'app-sidebar-cmp',
    templateUrl: 'sidebar.component.html',
})

export class SidebarComponent implements OnInit {
    public menuItems: any[];
    public user: User;
    public image: Image;


    constructor(private readonly router: Router,
                private readonly imageService: ImageService,
                private readonly authenticationService: AuthenticationService) {
        this.image = new Image();
        this.image.downloadUrl = './assets/img/faces/marc.jpg';
    }


    public isMobileMenu = () => {
        return $(window).width() <= 991;
    }
    public ngOnInit(): void {
        this.menuItems = ROUTES.filter(menuItem => menuItem.show);
        const currentUser = this.authenticationService.currentUserValue;
        if (currentUser && currentUser.token) {
            this.user = currentUser;
            if (this.user.imageInfo !== null ) {
                this.user.profileImage = this.user.imageInfo.downloadUrl;
            }

        }
    }

    public updatePS(): void {
        if (window.matchMedia(`(min-width: 960px)`).matches && !this.isMac()) {
            const elemSidebar = <HTMLElement>document.querySelector('.sidebar .sidebar-wrapper');
            const ps = new PerfectScrollbar(elemSidebar, {wheelSpeed: 2, suppressScrollX: true});
        }
    }

    public isMac(): boolean {
        let bool = false;
        if (navigator.platform.toUpperCase().indexOf('MAC') >= 0 ||
            navigator.platform.toUpperCase().indexOf('IPAD') >= 0) {
            bool = true;
        }
        return bool;
    }

    public logout(): void {
        this.authenticationService.logout();
        this.router.navigateByUrl('/pages/login');
    }
    public onClickProfile = () => {
        this.router.navigateByUrl('/profile');

    }
}
