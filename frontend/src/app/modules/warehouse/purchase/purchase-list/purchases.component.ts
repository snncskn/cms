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
import { OrderFilter } from 'src/app/models/purchase/order-filter';
import { ExcelService } from 'src/app/services/general/excel.service';
declare var $: any;

@Component({
  selector: 'app-purchases',
  templateUrl: './purchases.component.html',
  styleUrls: ['./purchases.component.css']
})
export class PurchasesComponent implements OnInit {


  public displayedColumns = ['requestDate', 'requestNumber', 'name', 'totalQuantity',
                             'grandTotal', 'status', 'btn'];
  public dataSource = new MatTableDataSource<Order>();
  public dataSourcePurchaseRequest = new MatTableDataSource<Order>();
  public dataSourceRejectOrder = new MatTableDataSource<Order>();
  public dataSourceOrder = new MatTableDataSource<Order>();
  public dataSourceInvoice = new MatTableDataSource<Order>();
  public purchaseSize: number;
  public orderSize: number;
  public invoiceSize: number;
  public rejectSize: number;
  public page: Pageable;
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;
  public lowValue: number;
  public highValue: number;
  public form: FormGroup;
  public orderStatus: OrderStatus;
  public statusStyle: string;
  @ViewChild('TABLE') public table: ElementRef;
  public selectedStatus: string;
  public excelMode: boolean;
  public selectedSupplier: string;
  public supplierDetail: boolean;


  public onLoading: boolean;
  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;


  constructor(private readonly orderService: OrderService,
    private readonly router: Router,
    private readonly formBuilder: FormBuilder,
    private readonly datePipe: DatePipe,
    private readonly excelService: ExcelService,
    public toastr: ToastrManager) {
    this.supplierDetail = false;
    this.totalElement = 0;
    this.orderStatus = new OrderStatus();
    this.selectedStatus = 'PURCHASE_REQUEST';
    this.pageIndex = 0;
    this.pageSize = 25;
    this.lowValue = 0;
    this.highValue = 25;
    const firtDate = new Date();
    firtDate.setDate(firtDate.getDate() - 30 );
    this.form = this.formBuilder.group({
      rejectionReason: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      requestDateStart: [this.datePipe.transform(firtDate, 'yyyy-MM-dd')],
      requestDateEnd: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      requestTimeStart: ['00:00'],
      requestTimeEnd: ['23:59'],
      orderCreationDateStart: [this.datePipe.transform(firtDate, 'yyyy-MM-dd')],
      orderCreationDateEnd: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      orderCreationTimeStart: ['00:00'],
      orderCreationTimeEnd: ['23:59'],
      invoiceDateStart: [this.datePipe.transform(firtDate, 'yyyy-MM-dd')],
      invoiceDateEnd: [this.datePipe.transform(new Date(), 'yyyy-MM-dd')],
      invoiceTimeStart: ['00:00'],
      invoiceTimeEnd: ['23:59'],

    });
    this.statusStyle = 'PURCHASE_REQUEST';
    this.purchaseSize = 0;
    this.invoiceSize = 0;
    this.rejectSize = 0;
    this.orderSize = 0;

  }
  public ngOnInit(): void {
    this.filter('PURCHASE_REQUEST');
    this.filter('ORDER');
    this.filter('INVOICE');
    this.filter('REJECTED');
  }
  public list = () => {
    this.pageList(this.pageIndex, this.pageSize, true, this.selectedStatus);
  }
  public pageList = (page: number, size: number, first: boolean, status: string) => {

    this.page = new Pageable();
    this.page.page = page;
    this.page.size = size;
    const orderFilter = new OrderFilter();
    orderFilter.size = this.pageSize;
    orderFilter.page = this.pageIndex;
    if (status === 'PURCHASE_REQUEST') {


        orderFilter.startDate = this.datePipe.transform(this.form.value.requestDateStart , 'yyyy-MM-dd');
        orderFilter.startDate = this.datePipe.transform(orderFilter.startDate +
                                                      ' ' + this.form.value.requestTimeStart, 'yyyy-MM-dd HH:mm');

        orderFilter.endDate = this.datePipe.transform(this.form.value.requestDateEnd , 'yyyy-MM-dd');
        orderFilter.endDate = this.datePipe.transform(orderFilter.endDate +
                                                      ' ' + this.form.value.requestTimeEnd, 'yyyy-MM-dd HH:mm');

    } else if (status === 'ORDER') {
      orderFilter.startDate = this.datePipe.transform(this.form.value.orderCreationDateStart , 'yyyy-MM-dd');
      orderFilter.startDate = this.datePipe.transform(orderFilter.startDate +
                                                    ' ' + this.form.value.orderCreationTimeStart, 'yyyy-MM-dd HH:mm');

      orderFilter.endDate = this.datePipe.transform(this.form.value.orderCreationDateEnd , 'yyyy-MM-dd');
      orderFilter.endDate = this.datePipe.transform(orderFilter.endDate +
                                                    ' ' + this.form.value.orderCreationTimeEnd, 'yyyy-MM-dd HH:mm');

    } else if (status === 'INVOICE') {
      orderFilter.startDate = this.datePipe.transform(this.form.value.invoiceDateStart , 'yyyy-MM-dd');
      orderFilter.startDate = this.datePipe.transform(orderFilter.startDate +
                                                    ' ' + this.form.value.invoiceTimeStart, 'yyyy-MM-dd HH:mm');


      orderFilter.endDate = this.datePipe.transform(this.form.value.invoiceDateEnd , 'yyyy-MM-dd');
      orderFilter.endDate = this.datePipe.transform(orderFilter.endDate +
                                                    ' ' + this.form.value.invoiceTimeEnd, 'yyyy-MM-dd HH:mm');
    }
    orderFilter.status = status;
    if (this.excelMode) {
      orderFilter.size = 99999;
      this.orderService.xls(orderFilter).subscribe(data => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(data);
        a.download = this.selectedStatus + '.xls';
        a.click();
        this.excelMode = false;
      });
    } else {
      this.orderService.filter(orderFilter).subscribe(data => {
        if (status === 'PURCHASE_REQUEST') {
          this.dataSourcePurchaseRequest.data = data.data.orderListItemProjections.content;
        } else if (status === 'ORDER') {
          this.dataSourceOrder.data = data.data.orderListItemProjections.content;
        }   else if (status === 'REJECTED') {
          this.dataSourceRejectOrder.data = data.data.orderListItemProjections.content;
        } else if (status  === 'INVOICE') {
          this.dataSourceInvoice.data = data.data.orderListItemProjections.content;
        }
        if (data.data.invoiceCounts) {
          this.invoiceSize = data.data.invoiceCounts;
        }
        if (data.data.requestCounts) {
          this.purchaseSize = data.data.requestCounts;
        }
        if (data.data.orderCounts) {
          this.orderSize = data.data.orderCounts;
        }
        if (data.data.rejectCounts) {
          this.rejectSize = data.data.rejectCounts;
        }
        this.onLoading = true;
      });
    }
  }
  public onEdit = (uuid) => {

    this.router.navigateByUrl('/warehouse/purchase/' + uuid);
  }

  public onAdd(): void {

    this.router.navigateByUrl('/warehouse/purchase');
  }

  public onDelete = (uuid) => {
    this.orderService.delete(uuid).subscribe(data => {
      this.toastr.successToastr(data.type, data.text);
      this.router.navigateByUrl('/warehouse/purchases');
      this.pageList(this.pageIndex, this.pageSize, true, this.selectedStatus);

    }, error => {
    });
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
  public onApproveReject = (uuid: string, status: string, comment: string) => {
    this.orderStatus.orderUuid = uuid;
    this.orderStatus.rejectionReason = comment;
    this.orderStatus.status = status;
    this.orderService.approveReject(this.orderStatus ).subscribe(data => {
      this.list();
    }, error => {
      this.list();
    });

  }
  public approveDialog = (uuid: string, status: string, comment: string) => {
    const tht = this;
    swal({
      title: 'Are you sure ?',
      html: '<div class="form-group">' +
          '</div>',
      showCancelButton: true,
      confirmButtonText: 'Yes',
      cancelButtonText: 'No',
      confirmButtonClass: 'btn btn-success',
      cancelButtonClass: 'btn btn-danger',
      buttonsStyling: false
  }).then((result) => {
      if (result.value) {
        tht.onApproveReject(uuid, status, comment);
      }
  }).catch(swal.noop);
  }
  public reject = (uuid: string, status: string) => {
    const tht = this;
    swal({
      title: 'Rejection reason',
      html: '<div class="form-group">' +
          '<input id="input-field" type="text" formControlName="rejectionReason" class="form-control" />' +
          '</div>',
      showCancelButton: true,
      confirmButtonClass: 'btn btn-success',
      cancelButtonClass: 'btn btn-danger',
      buttonsStyling: false
  }).then((result) => {
      if (result.value) {
        const inputElement: HTMLInputElement = document.getElementById('input-field') as HTMLInputElement;
        tht.onApproveReject(uuid, status, inputElement.value);
      }
  }).catch(swal.noop);
  }

  public getClassRow = (row: any) => {
    if (row.status === 'PURCHASE_REQUEST') {
      return 'table-purchase';
    } else if (row.status === 'ORDER') {
      return 'table-order';
    } else if (row.status === 'REJECTED') {
      return 'table-reject';
    }
  }
  public getSupplierName = (row: any) => {
    return row.supplier[0].name;
  }
  public getStatus = (row: string) => {
    if (row === 'ORDER') {
      return 'Purchase Order';

    } else if (row === 'PURCHASE_REQUEST') {
      return 'Purchase Request';

    } else if (row === 'REJECTED') {
      return 'Reject Order';

    } else if (row === 'INVOICE') {
      return 'Invoice';

    } else if (row === 'UPDATED') {
      return 'Update';
    } else if (row === 'PARTIAL') {
      return 'Partial';
    }
  }
  public filter = (f: string, excel?: boolean) => {
    this.selectedStatus = f;
    if (excel) {
      this.excelMode = true;
    }
    this.pageList(this.pageIndex, this.pageSize, true, f);
  }

  public exportExcel(status: string): void {
    alert('a');
    this.selectedStatus = status;
    this.page = new Pageable();
    const orderFilter = new OrderFilter();
    orderFilter.size = 9999999;
    orderFilter.page = 0;
    if (this.selectedStatus === 'PURCHASE_REQUEST') {


        orderFilter.startDate = this.datePipe.transform(this.form.value.requestDateStart , 'yyyy-MM-dd');
        orderFilter.startDate = this.datePipe.transform(orderFilter.startDate +
                                                      ' ' + this.form.value.requestTimeStart, 'yyyy-MM-dd HH:mm');

        orderFilter.endDate = this.datePipe.transform(this.form.value.requestDateEnd , 'yyyy-MM-dd');
        orderFilter.endDate = this.datePipe.transform(orderFilter.endDate +
                                                      ' ' + this.form.value.requestTimeEnd, 'yyyy-MM-dd HH:mm');

    } else if (this.selectedStatus === 'ORDER') {
      orderFilter.startDate = this.datePipe.transform(this.form.value.orderCreationDateStart , 'yyyy-MM-dd');
      orderFilter.startDate = this.datePipe.transform(orderFilter.startDate +
                                                    ' ' + this.form.value.orderCreationTimeStart, 'yyyy-MM-dd HH:mm');

      orderFilter.endDate = this.datePipe.transform(this.form.value.orderCreationDateEnd , 'yyyy-MM-dd');
      orderFilter.endDate = this.datePipe.transform(orderFilter.endDate +
                                                    ' ' + this.form.value.orderCreationTimeEnd, 'yyyy-MM-dd HH:mm');

    } else if ( this.selectedStatus === 'INVOICE') {
      orderFilter.startDate = this.datePipe.transform(this.form.value.invoiceDateStart , 'yyyy-MM-dd');
      orderFilter.startDate = this.datePipe.transform(orderFilter.startDate +
                                                    ' ' + this.form.value.invoiceTimeStart, 'yyyy-MM-dd HH:mm');


      orderFilter.endDate = this.datePipe.transform(this.form.value.invoiceDateEnd , 'yyyy-MM-dd');
      orderFilter.endDate = this.datePipe.transform(orderFilter.endDate +
                                                    ' ' + this.form.value.invoiceTimeEnd, 'yyyy-MM-dd HH:mm');
    }
    orderFilter.status = this.selectedStatus;
  /*  this.orderService.filter(orderFilter).subscribe(data => {
      this.excelService.exportAsExcelFile(data.data.content, 'Purchase');

    });
    */

  }
  public detailSupplier(row: Order): void {
    this.selectedSupplier = row.supplier[0].uuid;
    this.supplierDetail = true;
    $('#supplierDetail').modal('show');
  }
  public setStyle(it: any): string {
    if ((it % 2) === 0) {
        return 'zebra';
    } else {
        return '';
    }
}
}
