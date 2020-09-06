import { Component, OnInit, AfterViewInit, ViewChild, Input, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { MatPaginator, MatSort, MatTableDataSource, MatDialog, MatSnackBar, VERSION, PageEvent }
  from '@angular/material';
import { FormBuilder, FormControl, FormGroup, Validators, FormArray } from '@angular/forms';
import { Pageable } from 'src/app/models/map/pagination';
import { ToastrManager } from 'ng6-toastr-notifications';
import { Workshop } from '../../../models/workshop/workshop';
import { BreakDownService } from 'src/app/services/workshop/break-down.service';
import { DatePipe } from '@angular/common';
import { BreakDownSearch } from 'src/app/models/workshop/break-down/break-down-search';
import { UserForm } from 'src/app/models/users/userForm';
import { UserService } from 'src/app/services/general/user.service';
import { Image } from 'src/app/models/general/image';
import { JobCardCreateUpdateForm } from 'src/app/models/workshop/job-card/create-job-card';
import { ImageService } from 'src/app/services/general/image.service';
import * as XLSX from 'xlsx';
import { debounceTime, tap, switchMap, finalize } from 'rxjs/operators';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { ExcelService } from 'src/app/services/general/excel.service';
import {Location} from '@angular/common';

declare const $: any;

@Component({
  selector: '/app-workshop',
  templateUrl: 'workshop.component.html',
  styleUrls: ['workshop.component.scss', 'workshop.component.css'],
})


export class WorkshopComponent implements OnInit, AfterViewInit {

  public displayedColumns = ['breakDownStartDate', 'jobCardDate',
    'lastUpdateDate', 'fleetNumber', 'reportNumber', 'jobCardStatus', 'siteName', 'btn'];

  public dataSource = new MatTableDataSource<Workshop>();
  public dataSourceImage = new MatTableDataSource<Image>();
  public operatorList: UserForm[];
  public supervisiorList: UserForm[];
  public dateState: number;
  public tempDate: Date;


  public page: Pageable;
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;
  public lowValue: number;
  public highValue: number;
  @Input() public pageSizeOptions: number[] = [10, 25, 50, 100];
  public form: FormGroup;
  public workShop: Workshop;
  public breakDown: FormGroup;
  public image: Image;
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
  @ViewChild('TABLE') public table: ElementRef;
  public et_users: UserForm[];
  public isLoading: boolean;
  public status: string[] = ['Active Breakdown Report', 'Active Job Card', 'Closed Job Card'];
  public  statusSelected = new FormControl();



  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;
  @ViewChild('fileImage') public fileImage: ElementRef;

  public showWaitInfo: boolean;
  public excelMode: boolean;



  constructor(private readonly breakDownService: BreakDownService,
    private readonly router: Router,
    private readonly userService: UserService,
    private readonly formBuilder: FormBuilder,
    private readonly imageService: ImageService,
    private readonly ngxService: NgxUiLoaderService,
    private readonly excelService: ExcelService,
    private readonly datePipe: DatePipe,
    public toastr: ToastrManager,
    public locatio: Location, ) {

    this.et_users = [];
    this.pageIndex = 0;
    this.pageSize = 25;
    this.lowValue = 0;
    this.highValue = 25;
    const firstDate = new Date();
    firstDate.setDate(firstDate.getDate() - 30);

    this.form = formBuilder.group({
      breakDownStartDate: [this.datePipe.transform(firstDate, 'yyyy-MM-dd'), [Validators.required]],
      breakDownEndDate: [this.datePipe.transform(new Date(), 'yyyy-MM-dd'), [Validators.required]],
      breakDownStartTime: ['00:00'],
      breakdownCloseTime: ['23:59'],
      siteName: [''],
      status: [''],
      fleetNo: [''],
      reportNumber: ['']
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

  }
  public ngOnInit(): void {
    this.pageList(this.pageIndex, this.pageSize, true);
    this.userService.userGroup(this.page, 'SUPERVISOR').subscribe(data => {
      this.supervisiorList = data.data.content;
    });
    this.userService.userGroup(this.page, 'OPERATOR').subscribe(data => {
      this.operatorList = data.data.content;
    });

    this.breakDown.valueChanges
      .pipe(
        debounceTime(500),
        tap(() => {
          this.et_users = [];
          this.isLoading = false;
        }),
        switchMap(value => this.userService.filter(this.page, value.operator ? value.operator : '*')
          .pipe(
            finalize(() => {
              this.isLoading = false;
            }),
          )
        )
      )
      .subscribe(data => {
        this.et_users = data.data.content;
        this.isLoading = true;
      }, error => {
        this.et_users = [];
      });


  }
  public ngAfterViewInit(): void {
    this.dataSource.sort = this.sortSubs;
    this.dataSource.paginator = this.paginatorSubs;

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

  public pageList = (page: number, size: number, first: boolean) => {
    this.page = new Pageable();
    this.page.page = page;
    this.page.size = size;
    const breakDownSearch = new BreakDownSearch();
    breakDownSearch.page = this.page.page;
    breakDownSearch.size = this.page.size;
    breakDownSearch.fleetNumber = this.form.value.fleetNo;
    breakDownSearch.reportNumber = this.form.value.reportNumber;
    breakDownSearch.status = '';
    if (this.statusSelected.value) {
      this.statusSelected.value.forEach(val => {
        breakDownSearch.status += val + ',';
      });
    }
    breakDownSearch.status =  breakDownSearch.status.substr(0, breakDownSearch.status.length - 1);
    breakDownSearch.site = this.form.value.siteName;

    breakDownSearch.breakDownStartDate = this.datePipe.transform(this.form.value.breakDownStartDate, 'yyyy-MM-dd');
    breakDownSearch.breakDownStartDate = this.datePipe.transform(breakDownSearch.breakDownStartDate +
      ' ' + this.form.value.breakDownStartTime, 'yyyy-MM-dd HH:mm');
    breakDownSearch.breakDownEndDate = this.datePipe.transform(this.form.value.breakDownEndDate, 'yyyy-MM-dd');
    breakDownSearch.breakDownEndDate = this.datePipe.transform(breakDownSearch.breakDownEndDate
      + ' ' + this.form.value.breakdownCloseTime, 'yyyy-MM-dd HH:mm');
    if ( this.excelMode) {
      this.page.size = 99999;
      this.breakDownService.xls(breakDownSearch).subscribe(data => {
          const a = document.createElement('a');
          a.href = URL.createObjectURL(data);
          a.download = 'WorkShop.xls';
          a.click();
          this.excelMode = false;
      });
    } else {
      this.breakDownService.allBreakDown(breakDownSearch).subscribe(data => {
        this.dataSource.data = data.data.content;
        if (first) {
          this.dataSource.paginator = this.paginatorSubs;
          this.dataSource.sort = this.sortSubs;
        }
        this.onLoading = true;
        this.totalElement = data.data.totalElements;
      }, error => {
        this.toastr.errorToastr('', 'List error');
      });
    }

  }
  public searchReport(): void {
    this.pageList(this.pageIndex, this.pageSize, false);
    this.dateState = 1;
    this.tempDate = this.form.value.breakDownStartDateown;
  }


  public applyFilter(prm?: string): void {
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = this.pageSize;
    this.excelMode = (prm === 'X' ? true : false);
    this.pageList(this.pageIndex, this.pageSize, false);
  }
  public openBrekDown(workShop: Workshop): void {
    this.router.navigateByUrl('/workshop/job-card-pdf/' + workShop.uuid);

  }
  public openBrekDownB(workShop: Workshop): void {
    this.router.navigateByUrl('/workshop/job-breakdown-pdf/' + workShop.uuid);

  }
  public viewOpenBrekDown(wShop: Workshop, type: string): void {
    this.breakDown.reset();
    this.workShop = wShop;
    this.avatars = [];
    wShop.imageInfos.forEach(img => {
      this.avatars.push(img);
    });
    this.breakDown.get('breakDownStartDate').setValue(wShop.breakDownStartDate);
    this.breakDown.get('fleetNo').setValue(wShop.fleetNumber);
    this.breakDown.get('currentMachineHours').setValue(wShop.currentMachineHours);
    this.breakDown.controls['fleetNo'].disable();
    this.breakDown.controls['operator'].disable();

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
  public genarateJobCard(): void {
    if (!this.breakDown.valid) {
      this.toastr.errorToastr('', 'Invalid error');
      return;
    }
    const jobCardCreateUpdateForm = new JobCardCreateUpdateForm();
    jobCardCreateUpdateForm.jobCardUuid = this.workShop.uuid;
    jobCardCreateUpdateForm.imageInfos = this.imageList;
    jobCardCreateUpdateForm.operatorUuid = this.breakDown.controls['operatorUuid'].value;
    this.breakDownService.createJobCard(jobCardCreateUpdateForm).subscribe(data => {
      this.searchReport();
      $('#viewBrekDown').modal('hide');
    }, error => {
      this.toastr.errorToastr('', 'Invalid value');
    });


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
      this.image.name = this.fileData.name;
      this.image.downloadUrl = data.data.downloadUrl;
      if (this.imageList.length === 0) {
        this.image.selected = true;
      } else {
        this.image.selected = false;
      }
      this.imageList.push(this.image);
      this.avatars.push(this.image);
      this.dataSourceImage.data = this.imageList;
      this.ngxService.stop();
      this.operationLock = false;
    });
  }
  public onImageDelete(uuid: string): void {
    this.ngxService.start();
    this.imageService.delete(uuid).subscribe(img => {
      this.avatars = this.avatars.filter(item => item.uuid !== uuid);
      this.toastr.successToastr('Image deleted', 'Success');
      this.avatars = this.avatars;
      if (this.avatars.length === 0) {
        this.image = new Image();
        this.image.downloadUrl = './assets/img/placeholder.jpg';
      }
      this.ngxService.stop();

    });

  }
  public checkStyle(item: Workshop): string {
    return '';
    //    return item.jobCardStatus;

  }
  public itemSelected(fieldName: string): void {
    this.et_users.forEach(item => {
      if (item.uuid === fieldName) {
        this.breakDown.get('operator').setValue(item.staffNumber);
        this.breakDown.get('operatorUuid').setValue(item.uuid);
        this.breakDown.get('userName').setValue(item.fullName);
        return;
      }
    });
  }
  public exportExcel(): void {
    const breakDownSearch = new BreakDownSearch();
    breakDownSearch.page = 0;
    breakDownSearch.size = 9999999;
    breakDownSearch.filter = this.form.value.search;
    breakDownSearch.breakDownStartDate = this.datePipe.transform(this.form.value.breakDownStartDate, 'yyyy-MM-dd');
    breakDownSearch.breakDownStartDate = this.datePipe.transform(breakDownSearch.breakDownStartDate +
      ' ' + this.form.value.breakDownStartTime, 'yyyy-MM-dd HH:mm');
    breakDownSearch.breakDownEndDate = this.datePipe.transform(this.form.value.breakDownEndDate, 'yyyy-MM-dd');
    breakDownSearch.breakDownEndDate = this.datePipe.transform(breakDownSearch.breakDownEndDate
      + ' ' + this.form.value.breakdownCloseTime, 'yyyy-MM-dd HH:mm');
    this.breakDownService.allBreakDown(breakDownSearch).subscribe(data => {

      this.excelService.exportAsExcelFile(data.data.content, 'WorkShop');
    });
  }
  public setStyle(it: any): string {
    if ((it % 2) === 0) {
      return 'zebra';
    } else {
      return '';
    }
  }
}
