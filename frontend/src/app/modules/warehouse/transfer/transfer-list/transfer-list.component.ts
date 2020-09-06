import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { MatTableDataSource, MatPaginator, MatSort, PageEvent } from '@angular/material';
import { Pageable } from 'src/app/models/pagination-generic';
import { FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { OrderService } from 'src/app/services/warehouse/order.service';
import { Router } from '@angular/router';
import { ToastrManager } from 'ng6-toastr-notifications';
import { Order } from 'src/app/models/purchase/order';
import { OrderStatus } from 'src/app/models/purchase/order-status';
import swal from 'sweetalert2';
import { DatePipe } from '@angular/common';
import { TransferFilterForm } from 'src/app/models/transfer/transfer-filter';
import { TransferListItemProjection } from 'src/app/models/transfer/transfer-item';
import { TransferService } from 'src/app/services/warehouse/transfer.service';



declare var $: any;

@Component({
  selector: 'app-transfer-list',
  templateUrl: './transfer-list.component.html',
  styleUrls: ['./transfer-list.component.css']
})
export class TransferListComponent implements OnInit {


  public displayedColumns = ['sourceOwner', 'transferDate', 'transferNumber',
      'sourceSite', 'targetSite', 'status', 'btn'];
  public dataSource = new MatTableDataSource<TransferListItemProjection>();
  public dataSourceRequest = new MatTableDataSource<TransferListItemProjection>();
  public dataSourcePartial = new MatTableDataSource<TransferListItemProjection>();
  public dataSourceTransfer = new MatTableDataSource<TransferListItemProjection>();
  public dataSourceDeliver = new MatTableDataSource<TransferListItemProjection>();
  public dataSourceRejected = new MatTableDataSource<TransferListItemProjection>();
  public requestSize: number;
  public transferSize: number;
  public partialSize: number;
  public deliverSize: number;
  public rejectedSize: number;
  public page: Pageable;
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;
  public lowValue: number;
  public highValue: number;
  public form: FormGroup;
  public orderStatus: OrderStatus;
  public statusStyle: string;
  public excelMode: boolean;
  @ViewChild('TABLE') public table: ElementRef;
  public selectedStatus: string;


  public onLoading: boolean;
  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;


  constructor(private readonly orderService: OrderService,
    private readonly router: Router,
    private readonly formBuilder: FormBuilder,
    private readonly transferService: TransferService,
    private readonly datePipe: DatePipe,
    public toastr: ToastrManager) {
    this.orderStatus = new OrderStatus();
    this.selectedStatus = 'REQUEST';
    this.pageIndex = 0;
    this.pageSize = 25;
    this.lowValue = 0;
    this.highValue = 25;
    this.rejectedSize = 0;
    this.requestSize = 0;
    this.transferSize = 0;
    this.deliverSize = 0;
    const firtDate = new Date();
    firtDate.setDate(firtDate.getDate() - 30 );

    this.form = this.formBuilder.group({
      rejectionReason: [null],
      requestDateStart: [this.datePipe.transform(firtDate, 'yyyy-MM-dd')],
      requestDateEnd: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      requestTimeStart: ['00:00'],
      requestTimeEnd: ['23:59'],

      orderCreationDateStart: [this.datePipe.transform(firtDate, 'yyyy-MM-dd')],
      orderCreationDateEnd: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      orderCreationTimeStart: ['00:00'],
      orderCreationTimeEnd: ['23:59'],

      transferDateStart: [this.datePipe.transform(firtDate, 'yyyy-MM-dd')],
      transferDateEnd: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      transferTimeStart: ['00:00'],
      transferTimeEnd: ['23:59'],
      partialDateStart: [this.datePipe.transform(firtDate, 'yyyy-MM-dd')],
      partialDateEnd: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      partialTimeStart: ['00:00'],
      partialTimeEnd: ['23:59'],
      deliverDateStart: [this.datePipe.transform(firtDate, 'yyyy-MM-dd')],
      deliverDateEnd: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      deliverTimeStart: ['00:00'],
      deliverTimeEnd: ['23:59'],
      rejectedDateStart: [this.datePipe.transform(firtDate, 'yyyy-MM-dd')],
      rejectedDateEnd: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      rejectedTimeStart: ['00:00'],
      rejectedTimeEnd: ['23:59'],
      transferNumber: [''],
      sourceSite: [''],
      targetSite: [''],
    });
    this.statusStyle = 'REQUEST';

  }
  public ngOnInit(): void {
    this.filter('REQUEST');
    this.filter('TRANSFER');
    this.filter('DELIVER');
    this.filter('REJECTED');

  }
  public pageList = (page: number, size: number, first: boolean, status: string) => {

    this.page = new Pageable();
    this.page.page = page;
    this.page.size = size;
    const transferFilterForm = new TransferFilterForm();
    transferFilterForm.size = this.pageSize;
    transferFilterForm.page = this.pageIndex;
    transferFilterForm.transferNumber = this.form.value.transferNumber;
    transferFilterForm.sourceSite = this.form.value.sourceSite;
    transferFilterForm.targetSite = this.form.value.targetSite;
    if (status === 'REQUEST') {


      transferFilterForm.startDate = this.datePipe.transform(this.form.value.requestDateStart , 'yyyy-MM-dd');
      transferFilterForm.startDate = this.datePipe.transform(transferFilterForm.startDate +
                                                      ' ' + this.form.value.requestTimeStart, 'yyyy-MM-dd HH:mm');

      transferFilterForm.endDate = this.datePipe.transform(this.form.value.requestDateEnd , 'yyyy-MM-dd');
      transferFilterForm.endDate = this.datePipe.transform(transferFilterForm.endDate +
                                                      ' ' + this.form.value.requestTimeEnd, 'yyyy-MM-dd HH:mm');

    } else if (status === 'TRANSFER') {
      transferFilterForm.startDate = this.datePipe.transform(this.form.value.transferDateStart , 'yyyy-MM-dd');
      transferFilterForm.startDate = this.datePipe.transform(transferFilterForm.startDate +
                                                    ' ' + this.form.value.transferTimeStart, 'yyyy-MM-dd HH:mm');

      transferFilterForm.endDate = this.datePipe.transform(this.form.value.transferDateEnd , 'yyyy-MM-dd');
      transferFilterForm.endDate = this.datePipe.transform(transferFilterForm.endDate +
                                                    ' ' + this.form.value.transferTimeEnd, 'yyyy-MM-dd HH:mm');

    } else if (status === 'PARTIAL') {
      transferFilterForm.startDate = this.datePipe.transform(this.form.value.partialDateStart , 'yyyy-MM-dd');
      transferFilterForm.startDate = this.datePipe.transform(transferFilterForm.startDate +
                                                    ' ' + this.form.value.partialTimeStart, 'yyyy-MM-dd HH:mm');

      transferFilterForm.endDate = this.datePipe.transform(this.form.value.partialDateEnd , 'yyyy-MM-dd');
      transferFilterForm.endDate = this.datePipe.transform(transferFilterForm.endDate +
                                                    ' ' + this.form.value.partialTimeEnd, 'yyyy-MM-dd HH:mm');
    } else if ( status === 'DELIVER') {
      transferFilterForm.startDate = this.datePipe.transform(this.form.value.deliverDateStart , 'yyyy-MM-dd');
      transferFilterForm.startDate = this.datePipe.transform(transferFilterForm.startDate +
                                                    ' ' + this.form.value.deliverTimeStart, 'yyyy-MM-dd HH:mm');

      transferFilterForm.endDate = this.datePipe.transform(this.form.value.deliverDateEnd , 'yyyy-MM-dd');
      transferFilterForm.endDate = this.datePipe.transform(transferFilterForm.endDate +
                                                    ' ' + this.form.value.deliverTimeEnd, 'yyyy-MM-dd HH:mm');
    } else if (status === 'REJECTED') {
      transferFilterForm.startDate = this.datePipe.transform(this.form.value.rejectedDateStart , 'yyyy-MM-dd');
      transferFilterForm.startDate = this.datePipe.transform(transferFilterForm.startDate +
                                                    ' ' + this.form.value.rejectedTimeStart, 'yyyy-MM-dd HH:mm');

      transferFilterForm.endDate = this.datePipe.transform(this.form.value.rejectedDateEnd , 'yyyy-MM-dd');
      transferFilterForm.endDate = this.datePipe.transform(transferFilterForm.endDate +
                                                    ' ' + this.form.value.rejectedTimeEnd, 'yyyy-MM-dd HH:mm');
    }
    transferFilterForm.status = status;
    if (this.excelMode) {
      transferFilterForm.size = 99999;
      this.transferService.xls(transferFilterForm).subscribe(data => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(data);
        a.download = 'Transfer.xls';
        a.click();
        this.excelMode = false;
      });
    } else {
      this.transferService.filter(transferFilterForm).subscribe(data => {
        if (status === 'REQUEST') {
          this.dataSourceRequest.data = data.data.transferListItemProjections.content;
        } else if (status === 'PARTIAL') {
         this.dataSourcePartial.data = data.data.transferListItemProjections.content;
        }   else if (status === 'TRANSFER') {
          this.dataSourceTransfer.data = data.data.transferListItemProjections.content;
        } else if (status === 'REJECTED') {
          this.dataSourceRejected.data = data.data.transferListItemProjections.content;
        } else if (status === 'DELIVER') {
          this.dataSourceDeliver.data =  data.data.transferListItemProjections.content;
        }
        if (data.data.requestCounts) {
          this.requestSize = data.data.requestCounts;

        }
        if (data.data.transferCounts) {
          this.transferSize = data.data.transferCounts;

        }
        if (data.data.rejectCounts) {
          this.rejectedSize = data.data.rejectCounts;

        }
        if (data.data.deliverCounts) {
          this.deliverSize = data.data.deliverCounts;

        }
        this.onLoading = true;
      });
     }
  }
  public onAdd(): void {

    this.router.navigateByUrl('/warehouse/transfer');
  }
  public onEdit(uuid: string): void {

    this.router.navigateByUrl('/warehouse/transfer/' + uuid);
  }
  public ngAfterViewInit = (): void => {
    this.dataSource.sort = this.sortSubs;
    this.dataSource.paginator = this.paginatorSubs;

  }
  public serverdata = (event?: PageEvent) => {

    if (event.pageIndex === this.pageIndex + 1) {
      this.lowValue = this.lowValue + this.pageSize;
      this.highValue = this.highValue + this.pageSize;
    } else if (event.pageIndex === this.pageIndex - 1) {
      this.lowValue = this.lowValue - this.pageSize;
      this.highValue = this.highValue - this.pageSize;
    } else {

    }
    this.pageIndex = event.pageIndex;
    this.pageList(event.pageIndex, event.pageSize, false, this.selectedStatus);
    return event;
  }
  public filter = (f: string, excel?: boolean) => {
    this.selectedStatus = f;
    if (excel) {
      this.excelMode = excel;

    }
    this.pageList(this.pageIndex, this.pageSize, true, f);
  }
  public setStyle(it: any): string {
    if ((it % 2) === 0) {
        return 'zebra';
    } else {
        return '';
    }
}
}
