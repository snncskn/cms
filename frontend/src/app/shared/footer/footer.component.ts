import { Component } from '@angular/core';

@Component({
    selector: 'app-footer-cmp',
    templateUrl: 'footer.component.html'
})

export class FooterComponent {
    public thisYear: Date = new Date();
}
