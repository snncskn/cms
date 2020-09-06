import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VehicleService } from 'src/app/services/vehicle/vehicle.service';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-break-down',
  templateUrl: './break-down.component.html',
  styleUrls: ['./break-down.component.scss']
})
export class BreakDownComponent implements OnInit {

  public form: FormGroup;
  constructor(private readonly activatedRouter: ActivatedRoute,
              private readonly vehicleService: VehicleService ) {}

  public ngOnInit(): void {
    this.activatedRouter.paramMap.subscribe(params => {
      this.vehicleService.find(params.get('id')).subscribe(data => {

      });

    });
  }

}
