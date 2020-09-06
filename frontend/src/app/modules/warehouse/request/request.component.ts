import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { MatTableDataSource, MatPaginator, MatSort, PageEvent } from '@angular/material';
import { Pageable } from 'src/app/models/pagination-generic';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { BreakDownService } from 'src/app/services/workshop/break-down.service';
import { RequestListFilterForm } from 'src/app/models/request/request-filter';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { RequestInfo } from 'src/app/models/request/request-info';
import { UserForm } from 'src/app/models/users/userForm';
import { debounceTime, tap, switchMap, finalize } from 'rxjs/operators';
import { UserService } from 'src/app/services/general/user.service';
import { ItemCreateRequest } from 'src/app/models/request/item-create-request';
import { JobCardItemDeliveredListInfo } from 'src/app/models/request/item-delivered-info';
import { ToastrManager } from 'ng6-toastr-notifications';
declare const $: any;

@Component({
  selector: 'app-request',
  templateUrl: './request.component.html',
  styleUrls: ['./request.component.css']
})
export class RequestComponent implements OnInit {

  public form: FormGroup;
  public requestfrm: FormGroup;
  public deliverfrm: FormGroup;
  public requestInfo: RequestInfo;
  public jobCardItemDeliveredListInfo: JobCardItemDeliveredListInfo;
  public itemCreateRequest: ItemCreateRequest;
  public dataSource = new MatTableDataSource<RequestInfo>();
  public dataSourceDeliveredInfos = new MatTableDataSource<JobCardItemDeliveredListInfo>();
  public displayedColumns = ['requestDate', 'requestedUser', 'jobCardNum', 'fleetNum',
  'partNumber', 'desc', 'status', 'deliveryTime', 'requestedQty', 'btn'];
  public displayedColumnsDelivered = ['deliveredDate', 'deliveredUserInfo', 'receivedUserInfo', 'deliveredQuantity'];

  public page: Pageable;
  public pageDetail: Pageable;
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;
  public pageDesc: Pageable;
  public totalElementDeliver: number;
  public pageIndexDesc: number;
  public pageSizeDeliver: number;
  public et_users: UserForm[];
  public isLoading: boolean;
  public lowValue: number;
  public highValue: number;
  public hidden: boolean;
  public excelMode: boolean;
  @Input() public pageSizeOptions: number[] = [10, 25, 50, 100];
  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;

  @ViewChild('paginatorSubs') public paginatorSubsItem: MatPaginator;
  @ViewChild('sortSubs') public sortSubsItem: MatSort;

  constructor(private readonly formBuilder: FormBuilder,
    private readonly activatedRouter: ActivatedRoute,
    private readonly router: Router,
    private readonly userService: UserService,
    public toastr: ToastrManager,
    private readonly breakDownService: BreakDownService,
    private readonly datePipe: DatePipe) {
    this.itemCreateRequest = new ItemCreateRequest();
    this.pageDetail = new Pageable();
    this.pageDetail.page = 0;
    this.pageDetail.size = 99;
    this.pageIndexDesc = 0;
    this.pageSizeDeliver = 5;
    this.pageIndex = 0;
    this.pageSize = 10;
    const firstDate = new Date();
    firstDate.setDate(firstDate.getDate() - 30);

    this.et_users = [];
    this.requestInfo = new RequestInfo();
    this.form = formBuilder.group({
      requestStartDate: [this.datePipe.transform(firstDate, 'yyyy-MM-dd'), [Validators.required]],
      requestCloseDate: [this.datePipe.transform(new Date(), 'yyyy-MM-dd'), [Validators.required]],
      requestStartTime: ['00:00'],
      requestCloseTime: ['23:59'],
      jobCardNumber: [''],
      fleetNumber: [''],
      stockCode: [''],
      requestUser: [''],
      status: [''],
      itemDescription: ['']
    });

    this.requestfrm = formBuilder.group({
      stockCode: [null],
      barcode: [null],
      description: [null],
      requestedQuantity: [null],
      availableQuantity: [null],
      deliveredQuantity: [null],
      deliverQuantity: [null, [Validators.min(1)]],
      remainingQuantity: [0],
      operator: [null],
      operatorUuid: [null, [Validators.required]],
      userName: [null]
    });

    this.deliverfrm = formBuilder.group({
      stockCode: [null],
      requestedQuantity: [null],
      deliveredQuantity: [null],
      remainingQuantity: [null],
      barcode: [null],
      reciever: [null],
      description: [null],
    });
  }

  public ngOnInit(): void {
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = 9999;
    this.requestfrm.valueChanges
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
    this.applyFilter();
  }

  public onBack(): void {
    this.router.navigateByUrl('/warehouse/request');

  }
  public applyFilter(prm?: boolean): void {
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = this.pageSize;
    if (prm) {
      this.excelMode = true;
    }
    this.pageList(this.pageIndex, this.pageSize, true);
  }

  public serverDataDeliver(event?: PageEvent): any {
    if (event.pageIndex === this.pageIndex + 1) {
      this.lowValue = this.lowValue + this.pageSizeDeliver;
      this.highValue = this.highValue + this.pageSizeDeliver;
    } else if (event.pageIndex === this.pageIndex - 1) {
      this.lowValue = this.lowValue - this.pageSizeDeliver;
      this.highValue = this.highValue - this.pageSizeDeliver;
    }

    this.pageListDeliver(event.pageIndex , this.pageSizeDeliver, false);
    this.pageIndex = event.pageIndex;
    return event;
  }
  public pageListDeliver(page: number, size: number, first: boolean): void {
    this.pageDetail = new Pageable();
    this.pageDetail.page = page;
    this.pageDetail.size = size;
    this.breakDownService.findJobCardItemDelivered(this.requestInfo.uuid, this.pageDetail).subscribe(list => {
      this.dataSourceDeliveredInfos.data = list.data.content;
      this.totalElementDeliver = list.data.totalElements;
      if (first) {
        this.dataSourceDeliveredInfos.paginator = this.paginatorSubs;
        this.dataSourceDeliveredInfos.sort = this.sortSubs;
      }
    });
  }
  public viewDeliverItem(element: RequestInfo): void {
    this.requestfrm.reset();
    this.requestfrm.get('stockCode').setValue(element.stockCode);
    this.requestfrm.get('barcode').setValue(element.barcode);
    this.requestfrm.get('description').setValue(element.description);
    this.requestfrm.get('requestedQuantity').setValue(element.requestedQuantity);
    this.requestfrm.get('deliveredQuantity').setValue(element.deliveredQuantity);
    this.requestfrm.get('availableQuantity').setValue(element.availableQuantity);
    this.requestfrm.get('remainingQuantity').setValue(element.remainingQuantity);
    if ((element.requestedQuantity - element.deliveredQuantity)
                                           > element.availableQuantity) {
        this.requestfrm.controls['deliverQuantity'].setValidators([
                                            Validators.max(element.availableQuantity),
                                            Validators.min(1)]);
    } else {
      this.requestfrm.controls['deliverQuantity'].setValidators([
                                          Validators.max(element.requestedQuantity - element.deliveredQuantity),
                                          Validators.min(1)]);

    }

    this.requestfrm.controls['userName'].disable();
    this.requestfrm.controls['stockCode'].disable();
    this.requestfrm.controls['remainingQuantity'].disable();
    this.requestfrm.controls['barcode'].disable();
    this.requestfrm.controls['description'].disable();
    this.requestfrm.controls['requestedQuantity'].disable();
    this.requestfrm.controls['deliveredQuantity'].disable();
    this.requestfrm.controls['availableQuantity'].disable();
    this.requestfrm.controls['operator'].setValidators([Validators.required]);


    this.itemCreateRequest.jobCardItemUuid = element.uuid;
  }
  public itemSelected(fieldName: string): void {
    this.et_users.forEach(item => {
      if (item.uuid === fieldName) {
        this.requestfrm.get('operator').setValue(item.staffNumber + ' ' + item.fullName);
        this.requestfrm.get('operatorUuid').setValue(item.uuid);
        this.requestfrm.get('userName').setValue(item.fullName);
        return;
      }
    });
  }
  public showJobCard(element: RequestInfo): void {
    this.router.navigateByUrl('/workshop/job-card/' + element.uuid);

  }
  public deliverDetail(element: RequestInfo): void {
    this.pageDetail = new Pageable();
    this.pageDetail.page = 0;
    this.pageDetail.size = this.pageSizeDeliver;
    this.requestInfo = element;
    this.deliverfrm.reset();
    this.deliverfrm.get('stockCode').setValue(element.stockCode);
    this.deliverfrm.get('barcode').setValue(element.barcode);
    this.deliverfrm.get('description').setValue(element.description);
    this.deliverfrm.get('requestedQuantity').setValue(element.requestedQuantity);
    this.deliverfrm.get('deliveredQuantity').setValue(element.deliveredQuantity);
    this.deliverfrm.get('remainingQuantity').setValue(element.remainingQuantity);
    this.deliverfrm.controls['stockCode'].disable();
    this.deliverfrm.controls['barcode'].disable();
    this.deliverfrm.controls['description'].disable();
    this.deliverfrm.controls['requestedQuantity'].disable();
    this.deliverfrm.controls['deliveredQuantity'].disable();
    this.deliverfrm.controls['remainingQuantity'].disable();

    this.pageDetail = new Pageable();
    this.pageDetail.page = 0;
    this.pageDetail.size = this.pageSizeDeliver;
    this.pageListDeliver(this.pageDetail.page, this.pageDetail.size, true);
  }
  public serverDataDesc(event?: PageEvent): any {

    if (event.pageIndex === this.pageIndex + 1) {
      this.lowValue = this.lowValue + this.pageSize;
      this.highValue = this.highValue + this.pageSize;
    } else if (event.pageIndex === this.pageIndex - 1) {
      this.lowValue = this.lowValue - this.pageSize;
      this.highValue = this.highValue - this.pageSize;
    }
    this.pageIndex = event.pageIndex;
    this.pageList(this.pageIndex, this.pageSize, false);
    return event;
  }
  public pageList(page: number, size: number, first: boolean): void {
    const filter = new RequestListFilterForm();

    filter.requestStartDate = this.datePipe.transform(this.form.value.requestStartDate, 'yyyy-MM-dd');
    filter.requestStartDate = this.datePipe.transform(filter.requestStartDate +
      ' ' + this.form.value.requestStartTime, 'yyyy-MM-dd HH:mm');

    filter.requestEndDate = this.datePipe.transform(this.form.value.requestCloseDate, 'yyyy-MM-dd');
    filter.requestEndDate = this.datePipe.transform(filter.requestEndDate
      + ' ' + this.form.value.requestCloseTime, 'yyyy-MM-dd HH:mm');

    filter.page = page;
    filter.size = size;
    filter.jobCardNumber = this.form.value.jobCardNumber;
    filter.fleetNumber = this.form.value.fleetNumber;
    filter.requestUser = this.form.value.requestUser;
    filter.stockCode = this.form.value.stockCode;
    filter.itemDescription = this.form.value.itemDescription;
    filter.status = this.form.value.status;
    if (this.excelMode) {
      filter.page = 0;
      filter.size = 99999;
      this.breakDownService.xlsRequest(filter).subscribe(data => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(data);
        a.download = 'Request.xls';
        a.click();
        this.excelMode = false;
      });
    } else {
      this.breakDownService.listAllRequestList(filter).subscribe(data => {
        this.dataSource.data = data.data.content;
        if (first) {
          this.dataSource.paginator = this.paginatorSubs;
          this.dataSource.sort = this.sortSubs;
      }
      this.totalElement = data.data.totalElements;
      });
    }

  }

  public release(): void {
    if (!this.requestfrm.valid) {
      this.toastr.errorToastr('', 'Required field');
      return;

    }
    this.itemCreateRequest.deliverQuantity = this.requestfrm.value.deliverQuantity;
    this.itemCreateRequest.deliveredQuantity = this.requestfrm.controls['deliveredQuantity'].value;
    this.itemCreateRequest.requestedQuantity = this.requestfrm.controls['requestedQuantity'].value;
    this.itemCreateRequest.receivedUserUuid = this.requestfrm.value.operatorUuid;
    this.itemCreateRequest.receivedUserUuid = this.requestfrm.value.operatorUuid;
    this.breakDownService.createJobCardItemRequest(this.itemCreateRequest).subscribe(data => {
      this.applyFilter();
      $('#deliverItem').modal('hide');
    });
  }
  public searchReport(): void {
    this.pageList(this.pageIndex, this.pageSize, false);
  }
  public setStyle(it: any): string {
    if ((it % 2) === 0) {
      return 'zebra';
    } else {
      return '';
    }
  }
}
