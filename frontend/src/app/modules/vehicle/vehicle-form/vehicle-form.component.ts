import {Component, OnInit, ElementRef, ViewChild, AfterContentInit, Input} from '@angular/core';
import {FormBuilder, FormArray, ValidatorFn, FormControl} from '@angular/forms';
import { Validators, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { VehicleTypeCreateUpdateForm } from '../../../models/vehicle/vehicle-type-create-update-form';
import { MatTableDataSource, ThemePalette, MatSort, MatPaginator } from '@angular/material';
import { Pageable } from 'src/app/models/map/pagination';
import { VehicleAttributeValue } from 'src/app/models/map/vehicle-attribute-value';
import { VehicleTypeAttributes, VehicleAttributeListDetailCreateForm } from 'src/app/models/map/pagination.type';
import { VehicleService } from 'src/app/services/vehicle/vehicle.service';
import { VehicleTypeService } from 'src/app/services/vehicle/vehicle-type.service';
import { ImageService } from 'src/app/services/general/image.service';
import { ToastrManager } from 'ng6-toastr-notifications';
import { Image } from 'src/app/models/general/image';
import { VehicleInfo, VehicleImage, Vehicle} from 'src/app/models/vehicle/vehicle-info';
import { MatDialog} from '@angular/material';
import { SiteService } from 'src/app/services/general/site.service';
import { SiteForm } from 'src/app/models/sites/site-form';
import { PropertyService } from 'src/app/services/general/property.service';
import { Property } from 'src/app/models/property/property';
import { PropertyForm } from 'src/app/models/property/property-form';
import { AuthenticationService } from 'src/app/services/general/authentication.service';
import { BreakDownSearch } from 'src/app/models/workshop/break-down/break-down-search';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { debounceTime, tap, switchMap, finalize } from 'rxjs/operators';
import {Workshop} from '../../../models/workshop/workshop';
import {BreakDown} from '../../../models/workshop/break-down/break-down';
import {PageEvent} from '@angular/material/paginator';
import { DatePipe } from '@angular/common';
import { VehicleUsedItemFilterForm } from 'src/app/models/vehicle/vehicle-item-filter';
import { VehicleItemInfo } from 'src/app/models/vehicle/vehicle-item-info';

declare interface TableWithCheckboxes {
  id?: number;
  name: string;
  value: string;
  list?: TableWithCheckboxes[];
}
declare const $: any;
@Component({
  selector: 'app-vehicle-form',
  templateUrl: 'vehicle-form.component.html',
  styleUrls: ['vehicle-form.component.scss', 'vehicle-form.component.css']

})

export class VehicleFormComponent implements OnInit, AfterContentInit {
  public displayedColumns = ['breakdowndate', 'jobcartdate',
    'reportNumber', 'status', 'btn'];
    public displayedColumnsItem = ['stockCode', 'description',
    'itemType', 'deliverDate', 'jobCardNumber', 'usedQty'];
  public vehicleTypeList: VehicleTypeCreateUpdateForm[];
  public form: FormGroup;
  public attributesPanel: boolean;
  public workShop: Workshop;
  public vehicle: Vehicle;
  public breakDown: FormGroup;
  public page: Pageable;
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;

  public totalElementItem: number;
  public pageIndexItem: number;
  public pageSizeItem: number;

  public lowValue: number;
  public highValue: number;
  public items: FormArray;
  public vehicleForm: FormGroup;
  public types: VehicleTypeAttributes;
  public vehicleAttributeValues: VehicleAttributeValue[];
  public fileData: File = null;
  public vehicleAttributeListDetailCreateForm: VehicleAttributeListDetailCreateForm;
  public operationLock: boolean;
  public image: Image;
  public itemInfo: VehicleInfo;
  public dataSource = new MatTableDataSource<VehicleTypeCreateUpdateForm>();
  public dataSource1 = new MatTableDataSource<BreakDown>();
  public dataSourceItem = new MatTableDataSource<VehicleItemInfo>();

  public siteDataSource = new MatTableDataSource<SiteForm>();
  @ViewChild('site') public site: ElementRef;
  public currentIndex: number;
  public speed = 5000;
  public infinite = true;
  public direction = 'right';
  public directionToggle = true;
  public autoplay = true;
  public carouselShow: boolean;
  public imgInfo: VehicleImage;
  public avatars = [];
  public propertys: PropertyForm[];
  public totalElementDesc: number;
  public totalElementDescItem: number;
  public isLoading: boolean;
  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;
  @ViewChild('fileImage') public fileImage: ElementRef;
  @Input() public pageSizeOptions: number[] = [5, 10, 25, 50, 100];
  @Input() public pageSizeOptionsItem: number[] = [5, 10, 25, 50, 100];

  constructor(private readonly formBuilder: FormBuilder,
              private readonly vehicleService: VehicleService,
              private readonly vehicleTypeService: VehicleTypeService,
              private readonly siteService: SiteService,
              private readonly imageService: ImageService,
              private readonly propertyService: PropertyService,
              private readonly activatedRouter: ActivatedRoute,
              private readonly authenticationService: AuthenticationService,
              private readonly router: Router,
              private readonly ngxService: NgxUiLoaderService,
              private readonly datePipe: DatePipe,
              private readonly dialog: MatDialog,
              public toastr: ToastrManager) {
    this.pageIndex = 0;
    this.pageSize = 25;
    this.lowValue = 0;
    this.highValue = 25;
    const firtDate = new Date();
    firtDate.setDate(firtDate.getDate() - 30 );

    this.form = formBuilder.group({
      breakDownStartDate: [this.datePipe.transform(firtDate, 'yyyy-MM-dd'), [Validators.required]],
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
    this.carouselShow = false;
    this.avatars = [];
    this.image = new Image();
    this.image.downloadUrl = './assets/img/placeholder.jpg';
    this.currentIndex = 0;



    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = 99;
    this.siteService.list(this.page).subscribe(data => {
      this.siteDataSource.data = data.data.content;
    });
    this.propertyService.findByGroupName(this.page, 'MachineUnit').subscribe(data => {
      this.propertys = data.data.content;
    });
    this.vehicleForm.controls['siteUuid'].setValue(
      this.authenticationService.currentUserValue.currentSite.uuid);

    this.image = new Image();
    this.image.downloadUrl = './assets/img/placeholder.jpg';
    this.vehicle = new Vehicle();
    this.pageIndex = 0;
    this.pageSize = 25;


  }

  public ngAfterContentInit(): void {
  }

  public createRegisterForm(): void {
    this.vehicleForm = this.formBuilder.group({
      uuid: [null],
      siteUuid: [null, Validators.required],
      fleetNo: [null, Validators.required],
      serialNo: [null, Validators.required],
      vinNo: [null, Validators.required],
      registrationNo: [null, Validators.required],
      vehicleTypeUuid: [null],
      vehicleType: [null],
      currentMachineHours: [null, Validators.required],
      serviceIntervalHours: [null, Validators.required],
      imageUuid: [null],
      lastServiceDate: [{value: null, disabled: true}],
      lastServiceHours: [{value: null, disabled: true}],
      usable: [true, Validators.required],
      unit: [null, Validators.required],
      fuelConsuption: [{value: null, disabled: true}],
      preUseCheckList: [{value: null, disabled: true}],
      startDate: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      endDate: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      startTime: ['00:00'],
      endTime: ['23:59'],
      startDateItem: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      endDateItem: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      startTimeItem: ['00:00'],
      endTimeItem: ['23:59'],
    });
  }


  public ngOnInit(): any {
    this.operationLock = false;
    this.editMode();
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = 100;
    this.vehicleTypeService.list(this.page).subscribe(data => {
      this.dataSource.data = data.data.content;
      this.vehicleTypeList = data.data.content;
    });
    this.vehicleForm.controls['vehicleType'].valueChanges
      .pipe(
        debounceTime(100),
        tap(() => {
          this.dataSource.data = [];
          this.isLoading = true;
        }),
        switchMap(value => this.vehicleTypeService.filter(this.page, value ? value : '*')
          .pipe(
            finalize(() => {
              this.isLoading = false;
            }),
          )
        )
      )
      .subscribe(data => {
        this.dataSource.data = data.data.content;
      }, error => {
        this.dataSource.data = [];
      });

    this.activatedRouter.paramMap.subscribe(params => {
      if (params.has('id')) {
        this.vehicleService.find(params.get('id')).subscribe(data => {
          this.vehicleForm.patchValue(data.data);
          this.onTypeChange(data.data.vehicleType.uuid);
          this.vehicle = data.data;
          this.pageList(this.pageIndex, this.pageSize, true);
          if (data.data.imageUuid) {
            this.imageService.find(data.data.imageUuid).subscribe(image => {
              this.image = image.data;
            });
          }
        });

      }
    });

  }
  public editMode(): void {
    this.activatedRouter.paramMap.subscribe(params => {
      if (params.has('id')) {
        this.vehicleService.findVehicleList(params.get('id')).subscribe(data => {
          this.ngxService.stop();
          this.vehicleForm.patchValue(data.data.vehicleInfo);
          this.itemInfo = data.data;
          this.vehicleForm.controls['vehicleType'].setValue(data.data.vehicleInfo.vehicleType.name);
          this.vehicleForm.controls['vehicleTypeUuid'].setValue(data.data.vehicleInfo.vehicleType.uuid);
          this.vehicleForm.controls['siteUuid'].setValue(data.data.vehicleInfo.siteInfo.uuid);
          this.onTypeChangeEdit(data.data.vehicleInfo.vehicleType.uuid);
          this.avatars = data.data.vehicleInfo.imageInfos;
          data.data.vehicleInfo.imageInfos.forEach(item => {
            if (item.selected) {
              this.image.downloadUrl = item.downloadUrl;
            }
          });
        });
      }
    });
  }
  public handleNumberChange = (val) => {
    const val_plus_one = parseInt(val, 10) + 1;
    try {
      this.vehicleForm.removeControl(val_plus_one.toString());
    } catch {
    }
    /*
    this.attributesList.forEach((num) => {
      const fc = new FormControl('');
      this.vehicleForm.addControl(num.toString(), fc);
    });
    */
  }

  public minSelectedCheckboxes(): ValidatorFn {
    const validator: ValidatorFn = (formArray: FormArray) => {

      const selectedCount = formArray.controls
        .map(control => control.value)
        .reduce((prev, next) => next ? prev + next : prev, 0);

      return selectedCount >= 1 ? null : { notSelected: 0 };
    };

    return validator;
  }

  public onTypeChangeEdit(uuid: string): void {
    this.vehicleTypeService.find(uuid).subscribe(data => {

      this.types = data.data;
      const tht = this;
      if (this.itemInfo !== undefined) {
        this.itemInfo.vehicleInfo.vehicleType.vehicleTypeAttributes.forEach(item => {
          tht.types.vehicleTypeAttributes.forEach(attr => {
            if (item.uuid === attr.uuid) {
              attr.selectedVehicleAttrUuid = item.selectedVehicleAttrUuid;
            }
          });
        });
      }
      this.attributesPanel = true;
    }, error => {
      this.attributesPanel = false;
    });
  }

  public onTypeChange(uuid: string): void {
    this.attributesPanel = false;
    this.ngxService.start();
    this.dataSource.data.forEach(item => {
      this.ngxService.stop();
      if (item.uuid === uuid) {
          this.vehicleForm.get('vehicleType').setValue(item.name);
          this.vehicleForm.get('vehicleTypeUuid').setValue(item.uuid);
          return;
      }
  }, error => {
    this.ngxService.stop();

  });

    this.vehicleTypeService.find(uuid).subscribe(data => {

      this.types = data.data;
      const tht = this;
      if (this.itemInfo !== undefined) {
        this.itemInfo.vehicleInfo.vehicleType.vehicleTypeAttributes.forEach(item => {
          tht.types.vehicleTypeAttributes.forEach(attr => {
            if (item.uuid === attr.uuid) {
              attr.selectedVehicleAttrUuid = item.selectedVehicleAttrUuid;
            }
          });
        });
      }
      this.attributesPanel = true;
    }, error => {
      this.attributesPanel = false;
    });
    this.buttonActiveChecker();
  }

  public onSubmit(): void {
    if (!this.vehicleForm.valid) {
      this.toastr.errorToastr('Please check required field', 'Valitadion error');
    } else {
      this.types.vehicleTypeUuid = this.vehicleForm.value.vehicleTypeUuid;
      this.vehicleService.save(this.vehicleForm.value).subscribe(data => {
        if (data.type === 'ERROR') {
          this.toast(data.type, data.text);
          return;

        }
        this.toast(data.type, data.text);
        this.vehicleForm.patchValue(data);
        this.types.vehicleTypeUuid = data.data.vehicleTypeUuid;
        this.types.vehicleUuid = data.data.uuid;
        this.types.vehicleAttributeListDetailCreateForm = [];
        this.vehicleAttributeListDetailCreateForm = new VehicleAttributeListDetailCreateForm();
        this.types.vehicleTypeAttributes.forEach(element => {
          this.vehicleAttributeListDetailCreateForm = new VehicleAttributeListDetailCreateForm();
          this.vehicleAttributeListDetailCreateForm.vehicleAttributeUuid = element.uuid;
          this.vehicleAttributeListDetailCreateForm.vehicleAttributeValueUuid = element.selectedVehicleAttrUuid;
          this.types.vehicleAttributeListDetailCreateForm.push(this.vehicleAttributeListDetailCreateForm);
        });
        this.imgInfo = new VehicleImage();
        this.imgInfo.vehicleUuid = data.data.uuid;
        this.imgInfo.imageInfos = this.avatars;
        this.vehicleService.createImageVehicle(this.imgInfo).subscribe(img => {
        });
        this.vehicleService.createVehicleList(this.types).subscribe(_data => {
        });
        this.router.navigateByUrl('/vehicle/vehicles');
      }, error => {
        this.operationLock = false;
      });
    }
  }

  public onFileChanged = (event) => {
    this.ngxService.start();
    this.operationLock = true;
    this.fileData = <File>event.target.files[0];
    const mimeType = this.fileData.type;
    if (mimeType.match(/image\/*/) == null) {
      this.toastr.warningToastr('Only images are supported', 'File Type');
      this.image = new Image();
      this.image.downloadUrl = './assets/img/placeholder.jpg';
      this.fileImage.nativeElement.value = '';
      this.operationLock = false;
      this.avatars.forEach(i => {
        if (i.selected) {
          this.image.downloadUrl = i.downloadUrl;
        }
      });
      this.ngxService.stop();
      return;
    }
    if (this.fileData.size > 1048576) {
      this.toastr.warningToastr('File size max: 1MB ', 'File Size');
      this.image = new Image();
      this.image.downloadUrl = './assets/img/placeholder.jpg';
      this.fileImage.nativeElement.value = '';
      this.operationLock = false;
      this.avatars.forEach(i => {
        if (i.selected) {
          this.image.downloadUrl = i.downloadUrl;
        }
      });
      this.ngxService.stop();

      return;
    }
    this.imageService.upload(this.fileData).subscribe(data => {
      this.image = new Image();
      this.image.uuid = data.data.uuid;
      this.image.downloadUrl = data.data.downloadUrl;
      if (this.avatars.length === 0) {
        this.image.selected = true;
      } else {
        this.image.selected = false;
      }
      this.avatars.push(this.image);
      this.vehicleForm.controls['imageUuid'].setValue(data.data.uuid);

      this.operationLock = false;
      this.ngxService.stop();
    }, error => {
      this.ngxService.stop();
    });
  }

  public onModal(): void {
    this.dialog.open(VehicleFormComponent);
  }

  public onBack(): void {
    this.router.navigateByUrl('/vehicle/vehicles');
  }

  public toast(type: string, msg: string): void {
    if (type === 'SUCCESS') {
      this.toastr.successToastr(msg, 'Success!');
    } else if (type === 'WARNING') {
      this.toastr.warningToastr(msg, 'Warning!');
    } else if (type === 'INFO') {
      this.toastr.infoToastr(msg, 'Info!');
    } else if (type === 'ERROR') {
      this.toastr.errorToastr(msg, 'Error!');
    }
  }

  public displayErrorControl = (field: string, lent: number) => {
    if (this.vehicleForm.controls[field].value) {
      const s = '' + this.vehicleForm.controls[field].value;
      if (s.length >= lent) {
        return true;
      } else {
        return false;

      }
    } else {
      return false;
    }
  }

  public buttonActiveChecker(): void {
    const state = document.getElementById('button');
    if (this.operationLock === false) {
      state.setAttribute('style', 'background-color:purple');
    }
  }
  public onImageDelete(uuid: string): void {
    this.ngxService.start();
    this.imageService.delete(uuid).subscribe(img => {
      this.avatars = this.avatars.filter(item => item.uuid !== uuid);
      this.toastr.successToastr('Image deleted', 'Success');
      if (this.avatars.length === 0) {
        this.image = new Image();
        this.image.downloadUrl = './assets/img/placeholder.jpg';
      }
      this.ngxService.stop();


    });

  }
  public onImageDefault(img: Image): void {
    if (this.avatars.length === 0) {
      return;
    }
    this.ngxService.start();

    this.avatars.forEach(i => {
      if (img.uuid === i.uuid) {
        i.selected = true;
      } else {
        i.selected = false;
      }
    });
    img.selected = true;
    this.image.downloadUrl = img.downloadUrl;
    this.imgInfo = new VehicleImage();
    this.imgInfo.vehicleUuid = this.vehicleForm.value.uuid;
    this.imgInfo.imageInfos = this.avatars;
    this.vehicleService.createImageVehicle(this.imgInfo).subscribe(itm => {
      this.ngxService.stop();

    }, error => {
      this.ngxService.stop();
    } );

  }

  public onCarouselShow(): void {
    if (this.avatars.length === 0) {
      return;
    }
    this.carouselShow = true;
  }
  public onCarouselShowButton(): boolean {
    if (this.avatars.length === 0) {
      $('#myModal').modal('hide');
      return false;
    } else {
      return true;
    }
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
      this.dataSource1.data = data.data.content;
      if (first) {
        this.dataSource.paginator = this.paginatorSubs;
        this.dataSource.sort = this.sortSubs;
      }
      this.totalElementDesc = data.data.totalElements;

    });
  }

  public pageListItem(page: number, size: number, first: boolean): void {
    const vehicleUsedItemFilterForm = new VehicleUsedItemFilterForm();
    vehicleUsedItemFilterForm.startDate = this.datePipe.transform(this.vehicleForm.value.startDateItem, 'yyyy-MM-dd');
    vehicleUsedItemFilterForm.startDate = this.datePipe.transform(vehicleUsedItemFilterForm.startDate +
        ' ' + this.vehicleForm.value.startTimeItem, 'yyyy-MM-dd HH:mm')
    ;
    vehicleUsedItemFilterForm.endDate = this.datePipe.transform(this.vehicleForm.value.endDateItem, 'yyyy-MM-dd');
    vehicleUsedItemFilterForm.endDate = this.datePipe.transform(vehicleUsedItemFilterForm.endDate +
        ' ' + this.vehicleForm.value.endTime , 'yyyy-MM-dd HH:mm');
    vehicleUsedItemFilterForm.page = page;
    vehicleUsedItemFilterForm.size = size;
    vehicleUsedItemFilterForm.vehicleUuid = this.vehicle.uuid;
    this.vehicleService.findAllUsedItem(vehicleUsedItemFilterForm).subscribe(data => {
      this.dataSourceItem.data = data.data.content;
      if (first) {
        this.dataSourceItem.paginator = this.paginatorSubs;
        this.dataSourceItem.sort = this.sortSubs;
      }
      this.totalElementDesc = data.data.totalElements;

    });
  }
  public filterBreakDown(): void {
    this.pageList(this.pageIndex, this.pageSize, true);
  }
  public filterItem(): void {
    this.pageListItem(this.pageIndex, this.pageSize, true);
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
  public openBrekDownB(workShop: Workshop): void {
    this.router.navigateByUrl('/workshop/job-breakdown-pdf/' + workShop.uuid);
  }

  public applyFilter(val: string): void {
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = this.pageSize;
    this.pageList(this.pageIndex, this.pageSize, false);
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
  public getServerDataItem = (event?: PageEvent) => {

    if (event.pageIndex === this.pageIndexItem + 1) {
      this.lowValue = this.lowValue + this.pageSize;
      this.highValue = this.highValue + this.pageSize;
    } else if (event.pageIndex === this.pageIndexItem - 1) {
      this.lowValue = this.lowValue - this.pageSize;
      this.highValue = this.highValue - this.pageSize;
    }
    this.pageIndex = event.pageIndex;
    this.pageListItem(event.pageIndex, event.pageSize, false);
    return event;
  }

}

