import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-field-error-display',
  templateUrl: './field-error-display.component.html',
  styleUrls: ['./field-error-display.component.css']
})
export class FieldErrorSettingsComponent {

  @Input() public errorMsg: string;
  @Input() public displayError: boolean;

}
