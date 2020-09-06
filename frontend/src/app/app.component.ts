import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { User } from './models/general/user';
import { AuthenticationService } from './services/general/authentication.service';
import { VehicleService } from './services/vehicle/vehicle.service';
import { SiteService } from './services/general/site.service';
import { Pageable } from './models/pagination-generic';


@Component({
  selector: 'app-my-app',
  templateUrl: './app.component.html'
})

export class AppComponent implements OnInit {
  public currentUser: User;
  public page: Pageable;
  private _router: Subscription;
  constructor(public router: Router,
    private readonly siteService: SiteService,
    private readonly authenticationService: AuthenticationService) {
      this.authenticationService.currentUser.subscribe(x => this.currentUser = x);
  }
  public logout(): void {
    this.authenticationService.logout();
    this.router.navigate(['/login']);
  }

  public ngOnInit(): void {
    const currentUser = this.authenticationService.currentUserValue;
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = 99;
    this.siteService.list(this.page).subscribe(data => {

    }, error => {
      this.authenticationService.logout();
      this.router.navigate(['pages/login']);
    });
    if (currentUser && currentUser.token) {
    } else {
      this.authenticationService.logout();
      this.router.navigate(['/pages/login']);
    }
    this._router = this.router.events.filter(event => event instanceof NavigationEnd).subscribe(
      (event: NavigationEnd) => {
        const body = document.getElementsByTagName('body')[0];
        const modalBackdrop = document.getElementsByClassName('modal-backdrop')[0];
        if (body.classList.contains('modal-open')) {
          body.classList.remove('modal-open');
          modalBackdrop.remove();
        }
      });
  }
}
