import { Component, OnInit, ElementRef } from '@angular/core';
import { Router, NavigationEnd, NavigationStart } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';

@Component({
  selector: 'app-layout',
  templateUrl: './auth-layout.component.html'
})
export class AuthLayoutComponent implements OnInit {
  private toggleButton: any;
  private sidebarVisible: boolean;
  private mobile_menu_visible: any = 0;
  private _router: Subscription;

  constructor(private readonly router: Router, private readonly element: ElementRef) {
      this.sidebarVisible = false;
  }
  public ngOnInit(): void {
    const navbar: HTMLElement = this.element.nativeElement;

    this.toggleButton = navbar.getElementsByClassName('navbar-toggler')[0];
    this._router = this.router.events.filter(event => event instanceof NavigationEnd).
                                          subscribe((event: NavigationEnd) => {
      this.sidebarClose();
    });
  }
  public sidebarOpen(): void {
      const toggleButton = this.toggleButton;
      const body = document.getElementsByTagName('body')[0];
      setTimeout(() => {
          toggleButton.classList.add('toggled');
      }, 500);
      body.classList.add('nav-open');

      this.sidebarVisible = true;
  }
  public sidebarClose(): void {
      const body = document.getElementsByTagName('body')[0];
      this.toggleButton.classList.remove('toggled');
      this.sidebarVisible = false;
      body.classList.remove('nav-open');
  }

  public sidebarToggle(): void {
    const body = document.getElementsByTagName('body')[0];
      if (this.sidebarVisible === false) {
          this.sidebarOpen();
          const $layer = document.createElement('div');
          $layer.setAttribute('class', 'close-layer');
          if (body.querySelectorAll('.wrapper-full-page')) {
              document.getElementsByClassName('wrapper-full-page')[0].appendChild($layer);
          } else if (body.classList.contains('off-canvas-sidebar')) {
              document.getElementsByClassName('wrapper-full-page')[0].appendChild($layer);
          }

          setTimeout(() => {
            $layer.classList.add('visible');
          }, 150);

          $layer.onclick = () => {
            body.classList.remove('nav-open');
            this.mobile_menu_visible = 0;
            $layer.classList.remove('visible');
            this.sidebarClose();
          };

          $layer.onclick.bind(this);

          body.classList.add('nav-open');
      } else {
        document.getElementsByClassName('close-layer')[0].remove();
          this.sidebarClose();
      }
  }
}