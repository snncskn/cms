import {Component, OnInit, Input, ViewChild, ElementRef} from '@angular/core';
import {FormGroup, FormBuilder, Validators, FormControl} from '@angular/forms';
import { VehicleService } from 'src/app/services/vehicle/vehicle.service';
import { VehicleTypeService } from 'src/app/services/vehicle/vehicle-type.service';
import { VehicleAttributeService } from 'src/app/services/vehicle/vehicle-attribute.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Image } from 'src/app/models/general/image';
import { ImageService } from 'src/app/services/general/image.service';
import { Vehicle } from 'src/app/models/vehicle/vehicle-info';
import { BreakDownSearch } from 'src/app/models/workshop/break-down/break-down-search';
import { BreakDown } from 'src/app/models/workshop/break-down/break-down';
import { MatTableDataSource, MatSort, MatPaginator } from '@angular/material';
import { DatePipe } from '@angular/common';
import {Workshop} from '../../../models/workshop/workshop';
import {Pageable} from '../../../models/map/pagination';
import {UserForm} from '../../../models/users/userForm';
import {PageEvent} from '@angular/material/paginator';
import {ToastrManager} from 'ng6-toastr-notifications';

@Component({
  selector: 'app-vehicle-card',
  templateUrl: './vehicle-card.component.html',
  styleUrls: ['./vehicle-card.component.css']
})
export class VehicleCardComponent implements OnInit {

  public displayedColumns = ['breakdowndate', 'jobcartdate',
  'reportNumber', 'status', 'btn'];
  public form: FormGroup;
  public vehicleForm: FormGroup;
  public vehicle: Vehicle;
  public breakDown: FormGroup;
  public workShop: Workshop;
  public image: Image;
  public dataSource = new MatTableDataSource<BreakDown>();
  public page: Pageable;
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;
  public lowValue: number;
  public highValue: number;
  public avatars = [];
  public imageList = [];
  public currentIndex: number;
  public speed = 5000;
  public infinite = true;
  public direction = 'right';
  public directionToggle = true;
  public autoplay = true;
  public carouselShow: boolean;
  public operationLock: boolean;
  public onLoading: boolean;
  public fileData: File = null;
  @ViewChild('TABLE')public table: ElementRef;
  public et_users: UserForm[];
  public isLoading: boolean;
  @Input() public pageSizeOptions: number[] = [5, 10, 25, 50, 100];
  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;
  public totalElementDesc: number;

  constructor(private readonly formBuilder: FormBuilder,
    private readonly vehicleService: VehicleService,
    private readonly vehicleTypeService: VehicleTypeService,
    private readonly vehicleTypeAttributeService: VehicleAttributeService,
    private readonly activatedRouter: ActivatedRoute,
    private readonly imageService: ImageService,
    private readonly datePipe: DatePipe,

    private readonly router: Router,
              public toastr: ToastrManager) {

  this.et_users = [];
  this.pageIndex = 0;
  this.pageSize = 25;
  this.lowValue = 0;
  this.highValue = 25;
  this.form = formBuilder.group({
    breakDownStartDate: [this.datePipe.transform(new Date(), 'yyyy-MM-dd'), [Validators.required]],
    breakDownEndDate: [this.datePipe.transform(new Date(), 'yyyy-MM-dd'), [Validators.required]],
    search: [null]
  });
  this.workShop = new Workshop();
  this.breakDown = this.formBuilder.group({
    description: [null],
    supervisor: [null],
    currentMachineHours: [null],
    fleetNo: new FormControl(null),
    vehicleUUid: new FormControl(null),
    operator: [null, [Validators.required]],
    operatorUuid: [null],
    userName: [null],
    breakDownStartDate: [this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm:ss'), [Validators.required]],
    reportType: [null],
    breakDwonReportNumber: [null],
  });
  this.page = new Pageable();
  this.page.page = 0;
  this.page.size = 999;
      this.createRegisterForm();
      this.image = new Image();
      this.image.downloadUrl = './assets/img/placeholder.jpg';
      this.vehicle = new Vehicle();
      this.pageIndex = 0;
      this.pageSize = 25;
  }
  public createRegisterForm(): void {
    this.vehicleForm = this.formBuilder.group({
      uuid: [null],
      fleetNo: [{value: null, disabled: true}, Validators.required],
      serialNo: [{value: null, disabled: true}, Validators.required],
      vinNo: [{value: null, disabled: true}, Validators.required],
      registrationNo: [{value: null, disabled: true}, Validators.required],
      vehicleTypeUuid: [{value: null, disabled: true}, Validators.required],
      currentMachineHours: [{value: null, disabled: true}, Validators.required],
      serviceIntervalHours: [{value: null, disabled: true}, Validators.required],
      lastServiceDay: [{value: null, disabled: true}],
      lastServiceHours: [{value: null, disabled: true}],
      fuelConsuption: [{value: null, disabled: true}],
      preUseCheckList: [{value: null, disabled: true}],
      startDate: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      endDate: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      startTime: ['00:00'],
      endTime: ['23:59'],
    });
  }

  public ngOnInit(): void {
    this.activatedRouter.paramMap.subscribe(params => {
      if (params.has('id')) {
        this.vehicleService.find(params.get('id')).subscribe(data => {
          this.vehicleForm.patchValue(data.data);
          this.vehicle = data.data;
          this.pageList(this.pageIndex, this.pageSize, true);
          if (data.data.imageUuid !== null) {
            this.imageService.find(data.data.imageUuid).subscribe(image => {
              this.image = image.data;
          });
            }
        });

      }
    });
  }
  public pageList(page: number, size: number, first: boolean): void {
    const breakDownSearch = new BreakDownSearch();
    breakDownSearch.breakDownStartDate = this.datePipe.transform(this.vehicleForm.value.startDate, 'yyyy-MM-dd');
    breakDownSearch.breakDownStartDate = this.datePipe.transform(breakDownSearch.breakDownStartDate +
                                                    ' ' + this.vehicleForm.value.startTime, 'yyyy-MM-dd HH:mm')
                                                                        ;
    breakDownSearch.breakDownEndDate = this.datePipe.transform(this.vehicleForm.value.endDate, 'yyyy-MM-dd');
    breakDownSearch.breakDownEndDate = this.datePipe.transform(breakDownSearch.breakDownEndDate +
                                                    ' ' + this.vehicleForm.value.endTime , 'yyyy-MM-dd HH:mm');
    breakDownSearch.page = page;
    breakDownSearch.size = size;
    breakDownSearch.vehicleUuid = this.vehicle.uuid;
    this.vehicleService.findAllBreakdown(breakDownSearch).subscribe(data => {
      this.dataSource.data = data.data.content;
      if (first) {
        this.dataSource.paginator = this.paginatorSubs;
        this.dataSource.sort = this.sortSubs;
      }
      this.totalElementDesc = data.data.totalElements;

    });
  }

  public filterBreakDown(): void {
    this.pageList(this.pageIndex, this.pageSize, true);
  }

  public openBrekDown(workShop: Workshop): void {
    this.router.navigateByUrl('/workshop/job-card-pdf/' + workShop.uuid);

  }


  public viewOpenBrekDown(wShop: Workshop, type: string): void {
    // this.breakDown.reset();
    this.workShop = wShop;
    this.breakDown.get('breakDownStartDate').setValue(wShop.breakDownStartDate);
    this.breakDown.get('fleetNo').setValue(wShop.fleetNumber);
    this.breakDown.get('currentMachineHours').setValue(wShop.currentMachineHours);
    this.breakDown.controls['fleetNo'].disable();

    this.breakDown.get('operator').setValue(wShop.operatorUser.staffNumber);
    this.breakDown.get('userName').setValue(wShop.operatorUser.fullName);
    this.breakDown.get('operatorUuid').setValue(wShop.operatorUser.uuid);

    this.breakDown.get('breakDwonReportNumber').setValue(wShop.reportNumber);
    this.breakDown.get('description').setValue(wShop.description);


    this.breakDown.controls['supervisor'].disable();
    this.breakDown.controls['breakDwonReportNumber'].disable();
    this.breakDown.controls['breakDownStartDate'].disable();
    this.breakDown.controls['description'].disable();
    this.breakDown.controls['supervisor'].disable();
    this.breakDown.controls['userName'].disable();
    this.breakDown.controls['operatorUuid'].disable();
    this.breakDown.controls['currentMachineHours'].disable();
    this.breakDown.get('supervisor').setValue(wShop.supervisorUser.fullName);

    this.page.page = 0;
    this.page.size = 9999;

    if (type === 'JOB') {
      this.router.navigateByUrl('/workshop/job-card/' + this.workShop.uuid);

    }
  }

  public getServerData = (event?: PageEvent) => {

    if (event.pageIndex === this.pageIndex + 1) {
      this.lowValue = this.lowValue + this.pageSize;
      this.highValue = this.highValue + this.pageSize;
    } else if (event.pageIndex === this.pageIndex - 1) {
      this.lowValue = this.lowValue - this.pageSize;
      this.highValue = this.highValue - this.pageSize;
    }
    this.pageIndex = event.pageIndex;
    this.pageList(event.pageIndex, event.pageSize, false);
    return event;
  }

  public applyFilter(val: string): void {
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = this.pageSize;
    this.pageList(this.pageIndex, this.pageSize, false);
  }



}
