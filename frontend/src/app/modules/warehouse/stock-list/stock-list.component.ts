import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { MatTableDataSource, MatPaginator, MatSort, PageEvent } from '@angular/material';
import { Pageable } from 'src/app/models/pagination-generic';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { BreakDownService } from 'src/app/services/workshop/break-down.service';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { RequestInfo } from 'src/app/models/request/request-info';
import { UserForm } from 'src/app/models/users/userForm';
import { UserService } from 'src/app/services/general/user.service';
import { ItemCreateRequest } from 'src/app/models/request/item-create-request';
import { JobCardItemDeliveredListInfo } from 'src/app/models/request/item-delivered-info';
import { ToastrManager } from 'ng6-toastr-notifications';
import { StockService } from 'src/app/services/warehouse/stock.service';
import { StockFilterForm } from 'src/app/models/stock/stock-filter';
import { StockHistoryInfo } from 'src/app/models/stock/stock-report';
declare const $: any;


@Component({
  selector: 'app-stock-list',
  templateUrl: './stock-list.component.html',
  styleUrls: ['./stock-list.component.css']
})
export class StockListComponent implements OnInit {

  public form: FormGroup;
  public requestfrm: FormGroup;
  public deliverfrm: FormGroup;
  public requestInfo: RequestInfo;
  public jobCardItemDeliveredListInfo: JobCardItemDeliveredListInfo;
  public itemCreateRequest: ItemCreateRequest;
  public dataSource = new MatTableDataSource<StockHistoryInfo>();
  public dataSourceDeliveredInfos = new MatTableDataSource<JobCardItemDeliveredListInfo>();
  public displayedColumns = ['createdDate', 'siteName', 'stockCode', 'itemDescription',
     'moveNumber', 'moveType', 'quantity', 'excel'];
  public page: Pageable;
  public pageDetail: Pageable;
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;
  public pageDesc: Pageable;
  public totalElementDesc: number;
  public pageIndexDesc: number;
  public pageSizeDesc: number;
  public et_users: UserForm[];
  public isLoading: boolean;
  public lowValue: number;
  public highValue: number;
  public excelMode: boolean;



  @Input() public pageSizeOptions: number[] = [25, 50, 100];
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
    private readonly stockService: StockService,
    private readonly datePipe: DatePipe) {
    this.itemCreateRequest = new ItemCreateRequest();
    this.pageDetail = new Pageable();
    this.pageDetail.page = 0;
    this.pageDetail.size = 99;
    this.pageIndexDesc = 0;
    this.pageSizeDesc = 25;
    this.pageIndex = 0;
    this.pageSize = 25;

    this.et_users = [];
    const firtDate = new Date();
    firtDate.setDate(firtDate.getDate() - 30);

    this.requestInfo = new RequestInfo();
    this.form = formBuilder.group({
      requestStartDate: [this.datePipe.transform(firtDate, 'yyyy-MM-dd'), [Validators.required]],
      requestCloseDate: [this.datePipe.transform(new Date(), 'yyyy-MM-dd'), [Validators.required]],
      requestStartTime: ['00:00'],
      requestCloseTime: ['23:59'],
      stockCode: [''],
      itemDescription: [''],
      siteName: [''],
      moveNumber: [''],
      quantity: [''],
      moveType: [''],
      createdDate: [''],
    });

    this.requestfrm = formBuilder.group({
      stockCode: [null],
      requestedQuantity: [null],
      deliveredQuantity: [null],
      deliverQuantity: [null],
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
  public pageList(page: number, size: number, first: boolean): void {
    this.pageDesc = new Pageable();
    this.pageDesc.page = page;
    this.pageDesc.size = size;
    const filter = new StockFilterForm();

    filter.startDate = this.datePipe.transform(this.form.value.requestStartDate, 'yyyy-MM-dd');
    filter.startDate = this.datePipe.transform(filter.startDate +
      ' ' + this.form.value.requestStartTime, 'yyyy-MM-dd HH:mm');

    filter.endDate = this.datePipe.transform(this.form.value.requestCloseDate, 'yyyy-MM-dd');
    filter.endDate = this.datePipe.transform(filter.endDate
      + ' ' + this.form.value.requestCloseTime, 'yyyy-MM-dd HH:mm');

    filter.page = this.page.page;
    filter.size = this.page.size;
    filter.stockCode = this.form.value.stockCode;
    filter.itemDescription = this.form.value.itemDescription;
    filter.siteName = this.form.value.siteName;
    filter.moveNumber = this.form.value.moveNumber;
    filter.quantity = this.form.value.quantity;
    filter.moveType = this.form.value.moveType;
    filter.createdDate = this.form.value.createdDate;
    if (this.excelMode) {
      this.stockService.excel(filter).subscribe(data => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(data);
        a.download = 'Stock.xls';
        a.click();
        this.excelMode = false;
      });
    } else {
      this.stockService.findAllStock(filter).subscribe(data => {
        this.dataSource.data = data.data.content;
        if (first) {
          this.dataSource.paginator = this.paginatorSubs;
          this.dataSource.sort = this.sortSubs;
        }
        this.totalElement = data.data.totalElements;
      });
    }
  }

  public serverData(event?: PageEvent): any {
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
