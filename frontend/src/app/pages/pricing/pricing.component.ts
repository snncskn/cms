import { Component, OnInit, OnDestroy } from '@angular/core';


@Component({
    selector: 'app-pricing-cmp',
    templateUrl: './pricing.component.html'
})

export class PricingComponent implements OnInit, OnDestroy {
    public thisYear: Date = new Date();
    public ngOnInit(): void {
      const body = document.getElementsByTagName('body')[0];
      body.classList.add('pricing-page');
      body.classList.add('off-canvas-sidebar');
    }
    public ngOnDestroy(): void {
      const body = document.getElementsByTagName('body')[0];
      body.classList.remove('pricing-page');
      body.classList.remove('off-canvas-sidebar');
    }
}
