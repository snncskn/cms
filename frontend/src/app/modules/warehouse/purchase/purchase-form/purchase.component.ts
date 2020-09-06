import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray, FormControl, AbstractControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrManager } from 'ng6-toastr-notifications';
import { MatTableDataSource, MatPaginator, MatSort, PageEvent } from '@angular/material';
import { BehaviorSubject } from 'rxjs';
import { SupplierService } from 'src/app/services/warehouse/supplier.service';
import { Pageable } from 'src/app/models/map/pagination.vehicle';
import { VehicleTypeService } from 'src/app/services/vehicle/vehicle-type.service';
import { VehicleTypeCreateUpdateForm } from 'src/app/models/vehicle/vehicle-type-create-update-form';
import { DatePipe } from '@angular/common';
import { OrderService } from 'src/app/services/warehouse/order.service';
import { Order } from 'src/app/models/purchase/order';
import { OrderItem } from 'src/app/models/purchase/order-item';
import { SupplierCreateUpdateForm } from 'src/app/models/suppliers/supplier-create-update-form';
import { debounceTime, tap, switchMap, finalize } from 'rxjs/operators';
import { SiteService } from 'src/app/services/general/site.service';
import { SiteForm } from 'src/app/models/sites/site-form';
import { PartItem } from 'src/app/models/item/item';
import { ItemService } from 'src/app/services/item/item.service';
import swal from 'sweetalert2';
import { OrderStatus } from 'src/app/models/purchase/order-status';
import { OrderItemMessage } from 'src/app/models/purchase/order-item-message';
import { ApproveQuantityItem, Item } from 'src/app/models/item/item-info';
import { OrderInvoiceForm } from 'src/app/models/purchase/order-invoice';
import { User } from 'src/app/models/general/user';
import { AuthenticationService } from 'src/app/services/general/authentication.service';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { ExcelService } from 'src/app/services/general/excel.service';
import { OrderItemDetailListItemProjection } from 'src/app/models/purchase/order-item-detail';

declare const $: any;

@Component({
  selector: 'app-purchase',
  templateUrl: './purchase.component.html',
  styleUrls: ['./purchase.component.css']
})
export class PurchaseComponent implements OnInit {

  public user: User;
  public form: FormGroup;
  public commentForm: FormGroup;
  public formItem: FormGroup;
  public operationLock: boolean;
  public displayedColumns = ['itemUuid', 'quantity', 'unitPrice', 'taxPercent',
    'taxTotal', 'discountPercent', 'total', 'btn'];
  public dataSource = new MatTableDataSource<Order>();
  public dataSourceOrderItem = new BehaviorSubject<AbstractControl[]>([]);

  public supplierDataSource = new MatTableDataSource<SupplierCreateUpdateForm>();
  public siteDataSource = new MatTableDataSource<SiteForm>();
  public vehicleTypeList: VehicleTypeCreateUpdateForm[];
  public rows: FormArray;
  public et_fields: PartItem[];
  public page: Pageable;
  public pageMessage: Pageable;
  public order: Order;
  public orderItem: OrderItem;
  public orderItemTmp: OrderItem;
  public items = new MatTableDataSource<PartItem>();
  public itemsAuto = [];
  public create: boolean;
  public addItemFrm: FormGroup;
  public filteredMovies: any;
  public isLoading = false;
  public errorMsg: string;
  public editMod: boolean;
  public orderStatus: OrderStatus;
  public orderMessage: OrderItemMessage;
  public orderItemMessages = new MatTableDataSource<OrderItemMessage>();
  public orderItemDetailListItemProjection = new MatTableDataSource<OrderItemDetailListItemProjection>();
  public approveQuantityItem: ApproveQuantityItem;
  public showPdfDiv: boolean;
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;
  public pageSizeMessage: number;
  public lowValue: number;
  public highValue: number;
  public selectedMessageOrder: string;



  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;
  public displayedColumnsMessage = ['message'];
  public displayedColumnsHistory = ['createdUser', 'createdDate', 'quantity', 'description' ];
  @ViewChild('paginatorSubsMsg') public paginatorSubsMsg: MatPaginator;
  @ViewChild('sortSubsMsg') public sortSubsMsg: MatSort;


  constructor(private readonly formBuilder: FormBuilder,
    private readonly activatedRouter: ActivatedRoute,
    private readonly orderService: OrderService,
    private readonly supplierService: SupplierService,
    private readonly ngxService: NgxUiLoaderService,
    private readonly siteService: SiteService,
    private readonly datePipe: DatePipe,
    private readonly vehicleTypeService: VehicleTypeService,
    private readonly itemService: ItemService,
    private readonly authenticationService: AuthenticationService,
    private readonly excelService: ExcelService,
    private readonly router: Router,
    public toastr: ToastrManager
  ) {
    this.ngxService.start();
    this.user = this.authenticationService.currentUserValue;
    this.rows = this.formBuilder.array([]);
    this.create = true;
    this.createRegisterForm();
    this.order = new Order();
    this.orderItem = new OrderItem();
    this.orderStatus = new OrderStatus();
    this.orderStatus.status = 'PURCHASE_REQUEST';
    this.pageIndex = 0;
    this.pageSize = 2;
    this.pageSizeMessage = 5;


    this.page = new Pageable();
    this.page.page = 0;
    this.supplierService.list(this.page).subscribe(data => {
      this.supplierDataSource.data = data.data.content;
      this.dataSource.paginator = this.paginatorSubs;
      this.dataSource.sort = this.sortSubs;
    });
    this.siteService.list(this.page).subscribe(data => {
      this.siteDataSource.data = data.data.content;
      this.dataSource.paginator = this.paginatorSubs;
      this.dataSource.sort = this.sortSubs;
    });
    this.itemService.filterAuto('*').subscribe(data => {
      this.items.data = data.data.content;
      this.itemsAuto = data.data.content;
    });
    this.editMod = false;
    this.showPdfDiv = false;
    this.ngxService.stop();
  }
  public filterStates = (item: any) => {
    return this.items.data.filter(state =>
      state.storePartNumber.toLowerCase().indexOf(item.toLowerCase()) === 0);
  }
  public createRegisterForm(): void {
    this.form = this.formBuilder.group({
      uuid: [null],
      supplier: [null, Validators.required],
      name: [null],
      address: [null],
      registerNumber: [null],
      taxNumber: [null],
      site: [null, Validators.required],
      requestDate: [this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm'), Validators.required],
      requestNumber: [null],
      orderNumber: [null],
      orderCreationDate: [null],
      invoiceDate: [null],
      invoiceNumber: [null, [Validators.maxLength(50)]],
      totalQuantity: [null],
      grandTotal: [null],
      totalAmount: [null],
      status: [null],
      controls: this.rows

    });
    this.form.controls['site'].setValue(this.authenticationService.currentUserValue.currentSite.uuid);

    this.commentForm = this.formBuilder.group({
      orderItemUuid: [null],
      message: [null],
      userName: [null],
    });
    this.form.controls['requestDate'].disable();
    this.form.controls['requestNumber'].disable();
    this.form.controls['orderNumber'].disable();
    this.form.controls['invoiceDate'].disable();
    this.form.controls['invoiceNumber'].disable();
    this.form.controls['orderCreationDate'].disable();

    this.form.controls['address'].disable();
    this.form.controls['registerNumber'].disable();
    this.form.controls['taxNumber'].disable();
    this.addItemFrm = this.formBuilder.group({
      itemUuid: new FormControl(null),
      storePartNumber: new FormControl(null, Validators.required),
      storePartNumberUuid: new FormControl(null, Validators.required),
      barcode: new FormControl(null, Validators.required),
      description: new FormControl(null, Validators.required),
      quantity: new FormControl(null, Validators.required),
      unitPrice: new FormControl(null, Validators.required),
      taxPercent: new FormControl(null, [Validators.max(100), Validators.maxLength(3)]),
      taxTotal: new FormControl(null),
      unit: new FormControl(null),
      discountPercent: new FormControl(null, [Validators.max(100), Validators.maxLength(3)]),
      discount: new FormControl(null),
      total: new FormControl(null, [Validators.required]),
      approveQuantity: new FormControl(0)

    });

  }
  public ngOnInit(): void {
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = 9999;
    this.vehicleTypeService.list(this.page).subscribe(data => {
      this.vehicleTypeList = data.data.content;
    });
    this.activatedRouter.paramMap.subscribe(params => {
      if (params.has('id')) {
        this.create = false;
        this.editMod = true;
        this.orderService.find(params.get('id')).subscribe(data => {
          this.form.patchValue(data.data);
          this.form.controls['invoiceDate'].
            setValue(this.datePipe.transform(data.data.invoiceDate, 'yyyy-MM-dd'));
          this.order = data.data;
          this.orderStatus.orderUuid = data.data.uuid;
          this.orderStatus.status = data.data.status;
          this.form.get('supplier').setValue(data.data.supplierInfo.uuid);
          this.supplierDataSource.data.forEach(spp => {
            if (spp.uuid === data.data.supplierInfo.uuid) {
              this.form.controls['address'].setValue(spp.address);
              this.form.controls['registerNumber'].setValue(spp.registerNumber);
              this.form.controls['taxNumber'].setValue(spp.taxNumber);
            }
          });
          this.form.get('site').setValue(data.data.siteInfo.uuid);

          this.form.controls['site'].disable();
          this.form.controls['supplier'].disable();
          data.data.orderItemInfos.forEach(item => {
            this.formItem = new FormGroup({
              uuid: new FormControl(null),
              itemUuid: new FormControl(null),
              storePartNumber: new FormControl(null, Validators.required),
              storePartNumberUuid: new FormControl(null),
              barcode: new FormControl(null, Validators.required),
              description: new FormControl(null, Validators.required),
              quantity: new FormControl(null, Validators.min(0.001)),
              unitPrice: new FormControl(null, Validators.min(0.001)),
              taxPercent: new FormControl(null, [Validators.max(100), Validators.maxLength(3)]),
              unit: new FormControl(null),
              taxTotal: new FormControl(null),
              discountPercent: new FormControl(null, [Validators.max(100), Validators.maxLength(3)]),
              discount: new FormControl(null),
              total: new FormControl(null, [Validators.required]),
              approveQuantity: new FormControl(0, [Validators.min(0.001), Validators.max(item.approveQuantity)])
            }, { updateOn: 'change' });

            this.formItem.controls['barcode'].disable();
            this.formItem.controls['description'].disable();
            this.formItem.controls['taxTotal'].disable();
            this.formItem.controls['total'].disable();
            this.formItem.controls['unit'].disable();
            if (this.editMod) {
              this.formItem.controls['quantity'].disable();
              this.formItem.controls['unitPrice'].disable();
              this.formItem.controls['taxPercent'].disable();
              this.formItem.controls['discountPercent'].disable();

            }
            this.user.roleInfos.forEach(role => {
              if (role.name === 'ROLE_ADMIN') {
                this.formItem.controls['quantity'].enable();
                this.formItem.controls['unitPrice'].enable();
                this.formItem.controls['taxPercent'].enable();
                this.formItem.controls['discountPercent'].enable();
              }
            });
            if (data.data.status === 'ORDER'
              || data.data.status === 'PARTIAL'
              || data.data.status === 'INVOICE') {
              this.displayedColumns = ['itemUuid', 'quantity', 'unitPrice', 'taxPercent',
                'taxTotal', 'discountPercent', 'total', 'approveQuantity', 'btn'];
              this.form.controls['invoiceDate'].enable();
              this.form.controls['invoiceNumber'].enable();
              this.form.controls['invoiceNumber'].setValidators(Validators.required);
              this.form.controls['invoiceDate'].setValidators(Validators.required);

              this.formItem.controls['quantity'].disable();
              this.formItem.controls['unitPrice'].disable();
              this.formItem.controls['taxPercent'].disable();
              this.formItem.controls['discountPercent'].disable();
            }
            if (data.data.status === 'INVOICE') {
              this.form.controls['invoiceDate'].disable();
              this.form.controls['invoiceNumber'].disable();
              this.formItem.controls['approveQuantity'].disable();
            }
            if (data.data.status === 'ORDER') {
              this.form.controls['invoiceDate'].enable();
              this.form.controls['invoiceNumber'].enable();
              this.formItem.controls['approveQuantity'].enable();
            }
            this.formItem.get('unitPrice').setValue('0');
            this.formItem.get('uuid').setValue(item.uuid);
            this.formItem.get('itemUuid').setValue(item.itemInfo.uuid);

            this.formItem.get('storePartNumberUuid').setValue(item.itemInfo.uuid);
            this.formItem.get('storePartNumber').setValue(item.itemInfo.storePartNumber);

            this.formItem.get('barcode').setValue(item.barcode);
            this.formItem.get('description').setValue(item.description);
            this.formItem.get('quantity').setValue(item.quantity);
            this.formItem.get('taxPercent').setValue(item.taxPercent);
            this.formItem.get('unitPrice').setValue(item.unitPrice);
            this.formItem.get('discountPercent').setValue(item.discountPercent);
            this.formItem.get('discount').setValue(item.discount);
            this.formItem.get('total').setValue(item.total);
            this.formItem.get('taxTotal').setValue(item.taxTotal);
            this.formItem.get('unit').setValue(item.unit);
            this.formItem.get('approveQuantity').setValue(item.approveQuantity);

            this.rows.push(this.formItem);
            this.dataSourceOrderItem.next(this.rows.controls);
          });
        });
      } else {
        this.form.get('status').setValue('PURCHASE_REQUEST');
        // this.addOrderItem();
      }
    });
    this.addItemFrm.valueChanges
      .pipe(
        debounceTime(500),
        tap(() => {
          this.et_fields = [];
          this.isLoading = true;
        }),
        switchMap(value => this.itemService.filterAuto(value.storePartNumber ? value.storePartNumber : '*')
          .pipe(
            finalize(() => {
              this.isLoading = false;
            }),
          )
        )
      )
      .subscribe(data => {
        this.et_fields = data.data.content;
      }, error => {
        this.et_fields = [];
      });


  }

  public getControl = (index, fieldName) => {
    const a = this.rows.at(index).get(fieldName) as FormControl;
    return this.rows.at(index).get(fieldName) as FormControl;
  }
  public onSubmit = (): void => {
    if (!this.form.valid) {
      this.toastr.errorToastr('ERROR', 'Field Required');
      return;
    }
    if (this.rows.length === 0) {
      this.toastr.errorToastr('ERROR', 'Please Add item');
      return;

    }
    this.order.uuid = this.form.value.uuid;
    this.order.orderCreationDate =
      this.datePipe.transform(this.form.get('orderCreationDate').value, 'yyyy-MM-dd HH:mm');
    this.order.invoiceDate = this.datePipe.transform(this.form.get('invoiceDate').value, 'yyyy-MM-dd HH:mm');
    this.order.requestDate = this.datePipe.transform(this.form.get('requestDate').value, 'yyyy-MM-dd HH:mm');
    this.order.requestNumber = this.form.get('requestNumber').value;
    this.order.orderNumber = this.form.get('orderNumber').value;
    this.order.invoiceNumber = this.form.get('invoiceNumber').value;
    this.order.status = this.form.value.status;
    this.order.addressDetail = this.form.get('address').value;
    this.order.totalQuantity = 0;
    this.rows.controls.forEach(frm => {
      this.order.totalQuantity = this.order.totalQuantity + frm.get('quantity').value;
    });
    this.order.discountTotal = 0;
    this.rows.controls.forEach(frm => {
      this.order.discountTotal = this.order.discountTotal +
        (frm.get('total').value * frm.get('discountPercent').value / 100);
    });
    this.order.taxTotal = 0;
    this.rows.controls.forEach(frm => {
      this.order.taxTotal = this.order.taxTotal +
        (frm.get('total').value * frm.get('taxPercent').value / 100);
    });
    this.order.grandTotal = this.getGrandTotal();
    this.order.siteUuid = this.form.value.site;
    if (this.form.value.supplier) {
      this.order.supplierUuid = this.form.value.supplier;

    } else {
      this.order.supplierUuid = this.order.supplierInfo.uuid;
    }
    if (this.form.value.site) {
      this.order.siteUuid = this.form.value.site;

    } else {
      this.order.siteUuid = this.order.siteInfo.uuid;
    }
    this.orderService.create(this.order).subscribe(data => {
      for (let i = 0; i < this.rows.length; i++) {
        this.orderItem.uuid = this.rows.at(i).get('uuid').value;
        this.orderItem.itemUuid = this.rows.at(i).get('storePartNumberUuid').value;
        this.orderItem.orderUuid = data.data.uuid;
        this.orderItem.barcode = this.rows.at(i).get('barcode').value;
        this.orderItem.description = this.rows.at(i).get('description').value;
        this.orderItem.quantity = this.rows.at(i).get('quantity').value;
        this.orderItem.unitPrice = this.rows.at(i).get('unitPrice').value;
        this.orderItem.discountPercent = this.rows.at(i).get('discountPercent').value;
        this.orderItem.total = this.rows.at(i).get('total').value;
        this.orderItem.taxPercent = this.rows.at(i).get('taxPercent').value;
        this.orderItem.taxTotal = (this.orderItem.total * this.orderItem.taxPercent) / 100;
        this.orderItem.unit = this.rows.at(i).get('unit').value;
        this.orderItem.approve = false;
        this.orderItem.approveQuantity = 0;
        this.orderService.orderItem(this.orderItem).subscribe(item => {
        });
      }
      this.toastr.successToastr(data.type, data.text);
      this.router.navigateByUrl('/warehouse/purchases');
    }, error => {
      this.toastr.errorToastr('ERROR', 'Error');
    });
  }
  public onBack(): void {

    this.router.navigateByUrl('/warehouse/purchases');
  }

  public onChange = ($event, index: number) => {
    const tht = this;
    this.items.data.forEach(item => {
      if (item.uuid === $event.value) {
        tht.addItemFrm.controls['barcode'].setValue(item.barcode);
        tht.addItemFrm.controls['description'].setValue(item.itemDescription);
        tht.addItemFrm.controls['storePartNumber'].setValue(item.storePartNumber);
        tht.addItemFrm.controls['storePartNumberUuid'].setValue(item.storePartNumber);
      }
    });
  }
  public onChangeSupp(val: string): void {
    const tht = this;
    this.supplierDataSource.data.forEach(it => {
      if (it.uuid === val) {
        tht.form.value.address = it.address;
        tht.form.value.registerNumber = it.address;
        tht.form.patchValue({
          address: it.address, registerNumber: it.registerNumber, taxNumber: it.taxNumber,
          requestDate: this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm')
        });

      }
    });

  }
  public onCalculator = (index: number) => {
    const tht = this;
    let ttl = 0;
    let ttlTax = 0;
    const qt = tht.rows.at(index).get('quantity').value;
    const quantity = parseFloat(qt);
    if (quantity < 0) {
      tht.rows.at(index).get('quantity').setValue(0);
    }
    const unitPrice = tht.rows.at(index).get('unitPrice').value;
    if (unitPrice < 0) {
      tht.rows.at(index).get('unitPrice').setValue(0);
      return;
    }
    const discountPercent = parseFloat(tht.rows.at(index).get('discountPercent').value);
    if (discountPercent < 0) {
      tht.rows.at(index).get('discountPercent').setValue(0);
      return;
    }
    const taxPercent = tht.rows.at(index).get('taxPercent').value;
    if (taxPercent < 0) {
      tht.rows.at(index).get('taxPercent').setValue(0);
      return;
    }
    if (quantity !== null && unitPrice !== null) {
      ttl = unitPrice * quantity;
      tht.rows.at(index).get('discount').setValue(ttl * discountPercent / 100);
      ttl = ttl - (ttl * discountPercent / 100);
      tht.rows.at(index).get('total').setValue(ttl);
      ttlTax = unitPrice * quantity;
      ttlTax = (ttl * taxPercent / 100);
      tht.rows.at(index).get('taxTotal').setValue(ttlTax);
    }
    // tht.controls.at(index).get('discountPercent').setValue(item.itemDescription);
    //  tht.controls.at(index).get('total').setValue(item.itemDescription);
  }
  public onBarcode = ($event) => {
    const tht = this;
    this.items.data.forEach(item => {
      if (item.uuid === $event.value) {
        tht.addItemFrm.controls['barcode'].setValue(item.barcode);
        tht.addItemFrm.controls['description'].setValue(item.itemDescription);
      }
    });
  }
  public onCalculatorModal(): void {
    const tht = this;
    let ttl = 0;
    let ttlTax = 0;
    const quantity = this.addItemFrm.value.quantity;
    if (quantity < 0) {
      this.addItemFrm.controls['quantity'].setValue(0);
    }
    const unitPrice = this.addItemFrm.value.unitPrice;
    if (unitPrice < 0 || unitPrice === null) {
      this.addItemFrm.controls['unitPrice'].setValue(0);
      return;
    }
    const discountPercent = this.addItemFrm.value.discountPercent;
    if (discountPercent < 0) {
      this.addItemFrm.controls['discountPercent'].setValue(0);
      return;
    }
    if (discountPercent > 100) {
      this.addItemFrm.controls['discountPercent'].setValue(100);
      return;
    }
    const taxPercent = this.addItemFrm.value.taxPercent;
    if (taxPercent < 0) {
      this.addItemFrm.controls['taxPercent'].setValue(0);
      return;
    }
    if (taxPercent > 100) {
      this.addItemFrm.controls['taxPercent'].setValue(100);
      return;
    }
    if (quantity !== null && unitPrice !== null) {
      ttl = unitPrice * quantity;
      ttl = ttl - (ttl * discountPercent / 100);
      this.addItemFrm.controls['total'].setValue(ttl);
      ttlTax = unitPrice * quantity;
      ttlTax = (ttl * taxPercent / 100);
      this.addItemFrm.controls['taxTotal'].setValue(ttlTax);
    }
  }
  public getGrandTotal = () => {
    let total = 0;
    let totalTax = 0;
    this.rows.controls.forEach(frm => {
      total = total + ((frm.get('quantity').value * frm.get('unitPrice').value));
      total = total - (total * frm.get('discountPercent').value / 100);

      totalTax = totalTax + ((frm.get('quantity').value *
        frm.get('unitPrice').value) * frm.get('taxPercent').value / 100);
    });
    return (total + totalTax);
  }
  public getTotal = () => {
    let total = 0;
    this.rows.controls.forEach(frm => {
      total = total + ((frm.get('quantity').value * frm.get('unitPrice').value));
      total = total - (total * frm.get('discountPercent').value / 100);


    });
    return total;
  }
  public getTotalTax = () => {
    let totalTax = 0;
    this.rows.controls.forEach(frm => {
      totalTax = totalTax + ((frm.get('quantity').value *
        frm.get('unitPrice').value) * frm.get('taxPercent').value / 100);
    });
    return totalTax;
  }
  public addOrderItem = () => {
    this.orderItem = new OrderItem();
    this.orderItem.itemUuid = null;
    this.orderItem.barcode = null;
    this.orderItem.description = null;
    this.orderItem.quantity = null;
    this.orderItem.unitPrice = null;
    this.orderItem.discountPercent = 0;
    // this.dataSourceOrderItem.push(this.orderItem);
    this.rows.push(new FormGroup({
      uuid: new FormControl(null),
      itemUuid: new FormControl(null, Validators.required),
      barcode: new FormControl(null, Validators.required),
      description: new FormControl(null, Validators.required),
      quantity: new FormControl(0, { updateOn: 'change' }),
      taxPercent: new FormControl(0, { updateOn: 'change' }),
      unitPrice: new FormControl(0, { updateOn: 'change' }),
      discountPercent: new FormControl(0, { updateOn: 'change' }),
      total: new FormControl(0, Validators.required),
      unit: new FormControl(0, Validators.required),
      taxTotal: new FormControl(0, Validators.required),
      approveQuantity: new FormControl(0)
    }, { updateOn: 'change' }));
    this.rows.controls['barcode'].disable();
    this.rows.controls['unit'].disable();
    this.rows.controls['description'].disable();
    this.rows.controls['total'].disable();
    this.rows.controls['taxTotal'].disable();

    this.dataSourceOrderItem.next(this.rows.controls);

  }
  public onDelete = (index: number, row: any) => {
    const _cont = this;
    swal({
      title: 'Are you sure ?',
      html: '<div class="form-group">' +
        '</div>',
      showCancelButton: true,
      confirmButtonClass: 'btn btn-success',
      cancelButtonClass: 'btn btn-danger',
      buttonsStyling: false
    }).then((result) => {
      if (result.value) {
        if (row.value.uuid === null) {
          _cont.rows.removeAt(index);
          _cont.dataSourceOrderItem.next(_cont.rows.controls);
        } else {
          _cont.orderService.deleteOrderItem(row.value).subscribe(data => {
            _cont.rows.removeAt(index);
            _cont.dataSourceOrderItem.next(_cont.rows.controls);
          });
        }
      }
    }).catch(swal.noop);
  }
  public onAdd = () => {
    if (!this.addItemFrm.valid) {
      if (this.addItemFrm.get('storePartNumberUuid').value === null) {
        this.toastr.errorToastr('ERROR', 'Invalid Part Number');
      }
      return;
    }
    $('#newItem').modal('hide');
    this.formItem = new FormGroup({
      uuid: new FormControl(null),
      itemUuid: new FormControl(null),
      storePartNumber: new FormControl(null, Validators.required),
      storePartNumberUuid: new FormControl(null),
      barcode: new FormControl(null, Validators.required),
      description: new FormControl(null, Validators.required),
      quantity: new FormControl(null, Validators.min(0.001)),
      unitPrice: new FormControl(null, Validators.min(0.001)),
      taxPercent: new FormControl(null, [Validators.max(100), Validators.maxLength(3)]),
      unit: new FormControl(null),
      taxTotal: new FormControl(null),
      discountPercent: new FormControl(null, [Validators.max(100), Validators.maxLength(3)]),
      discount: new FormControl(null),
      total: new FormControl(null, [Validators.required]),
      approveQuantity: new FormControl(0)
    }, { updateOn: 'change' });
    this.formItem.controls['barcode'].disable();
    this.formItem.controls['unit'].disable();
    this.formItem.controls['description'].disable();
    this.formItem.controls['taxTotal'].disable();
    this.formItem.controls['total'].disable();
    this.user.roleInfos.forEach(role => {
      if (role.name === 'ROLE_ADMIN') {
        this.formItem.controls['barcode'].enable();
        this.formItem.controls['unit'].enable();
        this.formItem.controls['description'].enable();
        this.formItem.controls['taxTotal'].enable();
        this.formItem.controls['total'].enable();
      }
    });
    this.orderItemTmp = new OrderItem();
    this.orderItemTmp = this.addItemFrm.value;

    this.formItem.get('unitPrice').setValue('0');
    this.formItem.get('itemUuid').setValue(this.addItemFrm.get('itemUuid').value);
    this.et_fields.forEach(item => {
      if (item.storePartNumber === this.addItemFrm.get('storePartNumber').value) {
        this.formItem.get('storePartNumberUuid').setValue(item.uuid);
        this.formItem.get('barcode').setValue(item.barcode);
        this.formItem.get('description').setValue(item.itemDescription);
      }
    });
    this.formItem.get('storePartNumberUuid').setValue(this.addItemFrm.get('storePartNumberUuid').value);
    this.formItem.get('storePartNumber').setValue(this.addItemFrm.get('storePartNumber').value);
    this.formItem.get('barcode').setValue(this.addItemFrm.get('barcode').value);
    this.formItem.get('description').setValue(this.addItemFrm.get('description').value);

    this.formItem.get('quantity').setValue(this.addItemFrm.get('quantity').value);
    this.formItem.get('taxPercent').setValue(this.addItemFrm.get('taxPercent').value);
    this.formItem.get('unitPrice').setValue(this.addItemFrm.get('unitPrice').value);
    this.formItem.get('discountPercent').setValue(this.addItemFrm.get('discountPercent').value);
    this.formItem.get('total').setValue(this.addItemFrm.get('total').value);
    this.formItem.get('taxTotal').setValue(this.addItemFrm.get('taxTotal').value);
    this.formItem.get('unit').setValue(this.addItemFrm.get('unit').value);
    let isRow = true;
    const tht = this;
    this.rows.controls.forEach((ss, index) => {
      if (ss.value.storePartNumber === tht.formItem.get('storePartNumber').value) {
        ss.get('quantity').setValue(ss.get('quantity').value + tht.formItem.get('quantity').value);
        tht.onCalculator(index);
        isRow = false;
      }
    });
    if (isRow) {
      this.rows.push(this.formItem);
      this.dataSourceOrderItem.next(this.rows.controls);

    }

    this.addItemFrm.reset();
  }

  public itemFilter(fieldName: string): void {
    this.et_fields.forEach(item => {
      if (item.storePartNumber === fieldName) {
        this.addItemFrm.get('storePartNumberUuid').setValue(item.uuid);
        this.addItemFrm.get('storePartNumber').setValue(item.storePartNumber);
        this.addItemFrm.get('barcode').setValue(item.barcode);
        this.addItemFrm.get('description').setValue(item.itemDescription);
        this.addItemFrm.get('unit').setValue(item.unit);
      }
    });
  }
  public newItem(): void {
    this.addItemFrm.reset();
    this.addItemFrm.get('discountPercent').setValue('0');
    this.addItemFrm.get('taxPercent').setValue('0');
    this.addItemFrm.get('taxTotal').setValue('0');
    this.addItemFrm.get('total').setValue('0');

    //    this.addItemFrm.get('quantity').setValue('0');
    //    this.addItemFrm.get('unitPrice').setValue('0');

    this.addItemFrm.controls['barcode'].disable();
    this.addItemFrm.controls['unit'].disable();
    this.addItemFrm.controls['description'].disable();
    this.addItemFrm.controls['taxTotal'].disable();
    this.addItemFrm.controls['total'].disable();

  }

  public approveDialog = (status: string) => {
    const tht = this;
    if (!this.form.valid) {
      this.toastr.errorToastr('', 'Required Field.');
      return;
    }

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
        tht.orderStatus.orderUuid = tht.form.value.uuid;
        tht.orderStatus.rejectionReason = '';
        tht.orderStatus.status = status;
        tht.orderService.approveReject(tht.orderStatus).subscribe(data => {
          if (status === 'INVOICE') {
            const orderInvoiceForm = new OrderInvoiceForm();
            orderInvoiceForm.uuid = tht.form.get('uuid').value;
            orderInvoiceForm.invoiceDate =
              this.datePipe.transform(this.form.get('invoiceDate').value, 'yyyy-MM-dd HH:mm');
            orderInvoiceForm.invoiceNumber = tht.form.get('invoiceNumber').value;
            tht.orderService.updateInvoiceInfo(orderInvoiceForm).subscribe(itm => {
            });
          }
          tht.onBack();
        });

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
        tht.orderStatus.orderUuid = tht.form.value.uuid;
        tht.orderStatus.rejectionReason = inputElement.value;
        tht.orderStatus.status = 'REJECTED';
        tht.orderService.approveReject(tht.orderStatus).subscribe(data => {
          tht.onBack();
        });
      }
    }).catch(swal.noop);
  }

  public newItemDesc(): void {
    alert('...');

  }
  public sendMessage(): void {
    if (!this.commentForm.valid) {
      this.toastr.errorToastr('ERROR', 'Message Required');
      return;
    }
    this.orderMessage = new OrderItemMessage();
    this.orderMessage.orderItemUuid = this.commentForm.get('orderItemUuid').value;
    this.orderMessage.message = this.commentForm.get('message').value;
    this.orderService.createOrderItemMessage(this.orderMessage).subscribe(data => {
      this.toastr.successToastr('Success', 'Message successfull');
      $('#commentItem').modal('hide');
      this.commentForm.get('message').setValue('');


    });
  }
  public onComment(index: number, row: any): void {
    $('#commentItem').modal('show');
    this.commentForm.get('orderItemUuid').setValue(row.controls.uuid.value);
    this.orderItemMessages.data = [];
    this.selectedMessageOrder = row.controls.uuid.value;
    this.pageList(this.pageIndex, this.pageSizeMessage, true, this.selectedMessageOrder);
  }
  public checkPermission(btn: string, obj: any): boolean {
    const status = this.orderStatus.status;
    if (btn === 'BTN_DELETE') {
      let isRow = false;
      if (status === 'ORDER'
           || status === 'INVOICE'
               || status === 'PARTIAL') {
        return false;
      }
      if (status === 'REJECTED'
        || status === 'PURCHASE_REQUEST') {
        isRow = false;
      } else {
      }
      this.user.roleInfos.forEach(role => {
        if (role.name === 'ROLE_ADMIN') {
          isRow = true;
        }
      });
      return isRow;

    } else if (btn === 'BTN_HISTORY' && this.editMod
                                  && status !== 'PURCHASE_REQUEST' ) {
      return true;

    } else if (btn === 'BTN_SAVE' && (status === 'PURCHASE_REQUEST'
      || status === '')) {
      return true;

    } else if (btn === 'BTN_MESSAGE') {
      if (obj.value.uuid === null) {
        return false;
      }
      if (status === 'PURCHASE_REQUEST') {
        return true;
      }
      if (!this.editMod) {
        return true;
      }
    } else if (btn === 'BTN_INVOICE' && status === 'ORDER') {
      return true;
    } else if (btn === 'BTN_REFRESH' && status !== 'INVOICE') {
      return true;
    } else if (btn === 'BTN_NEW' && status === 'PURCHASE_REQUEST') {
      return true;
    } else if (btn === 'BTN_REJECT' && this.editMod &&
      (status === 'PURCHASE_REQUEST')) {
      return true;
    } else if (btn === 'BTN_ORDER'
      && status === 'PURCHASE_REQUEST'
      && this.editMod) {
      return true;
    } else if (btn === 'BTN_PDF'
      && (status === 'PURCHASE_REQUEST'
        || status === 'ORDER'
        || status === 'INVOICE')
      && this.editMod) {
      return true;
    } else {
      return false;
    }
  }
  public onCreateApproveQuantity(orderItem: any): void {
    const _controller = this;
        swal({
            title: 'Are you sure?',
            html: '<div class=“form-group”>' +
                '</div>',
            showCancelButton: true,
            confirmButtonText: 'Yes',
            cancelButtonText: 'No',
            confirmButtonClass: 'btn btn-success',
            cancelButtonClass: 'btn btn-danger',
            buttonsStyling: false
        }).then(function (result: any): void {
            if (result.value) {
                if (orderItem.status === 'VALID') {
                  _controller.approveQuantityItem = new ApproveQuantityItem();
                  _controller.approveQuantityItem.approveQuantity = orderItem.controls.approveQuantity.value;
                  _controller.approveQuantityItem.orderItemUuid = orderItem.controls.uuid.value;
                  _controller.orderService.createApproveQuantity(_controller.approveQuantityItem).subscribe(appData => {
                    _controller.orderStatus.status = appData.data.orderStatus;
                    _controller.form.value.status = appData.data.orderStatus;
                    if (appData.data.orderStatus === 'ORDER') {
                      _controller.form.controls['invoiceNumber'].setValidators(Validators.required);
                      _controller.form.controls['invoiceDate'].setValidators(Validators.required);
                      }
                    if (appData.data.responseStatus === 'WARNING') {
                      _controller.toastr.warningToastr(appData.text, 'Warning');

                    } else if (appData.data.responseStatus === 'SUCCESS') {
                      _controller.toastr.successToastr(appData.text, 'Success');
                    }
                    _controller.orderService.find(_controller.order.uuid).subscribe(data => {
                      _controller.form.patchValue(data.data);
                      _controller.form.controls['invoiceDate'].
                        setValue(_controller.datePipe.transform(data.data.invoiceDate, 'yyyy-MM-dd'));
                        _controller.order = data.data;
                        _controller.orderStatus.orderUuid = data.data.uuid;
                        _controller.orderStatus.status = data.data.status;
                        _controller.form.get('supplier').setValue(data.data.supplierInfo.uuid);
                        _controller.supplierDataSource.data.forEach(spp => {
                        if (spp.uuid === data.data.supplierInfo.uuid) {
                          _controller.form.controls['address'].setValue(spp.address);
                          _controller.form.controls['registerNumber'].setValue(spp.registerNumber);
                        }
                      });
                      _controller.form.get('site').setValue(data.data.siteInfo.uuid);
                      _controller.form.controls['site'].disable();
                      _controller.form.controls['supplier'].disable();
                      _controller.rows.controls = [];
                      data.data.orderItemInfos.forEach(item => {
                        _controller.formItem = new FormGroup({
                          uuid: new FormControl(null),
                          itemUuid: new FormControl(null),
                          storePartNumber: new FormControl(null, Validators.required),
                          storePartNumberUuid: new FormControl(null),
                          barcode: new FormControl(null, Validators.required),
                          description: new FormControl(null, Validators.required),
                          quantity: new FormControl(null, Validators.min(0.001)),
                          unitPrice: new FormControl(null, Validators.min(0.001)),
                          taxPercent: new FormControl(null, [Validators.max(100), Validators.maxLength(3)]),
                          unit: new FormControl(null),
                          taxTotal: new FormControl(null),
                          discountPercent: new FormControl(null, [Validators.max(100), Validators.maxLength(3)]),
                          discount: new FormControl(null),
                          total: new FormControl(null, [Validators.required]),
                          approveQuantity: new FormControl(0)
                        }, { updateOn: 'change' });
                        if (item.approveQuantity > 0 ) {
                          _controller.formItem.controls['approveQuantity'].
                          setValidators(Validators.max(item.approveQuantity));

                        }

                        _controller.formItem.controls['barcode'].disable();
                        _controller.formItem.controls['description'].disable();
                        _controller.formItem.controls['taxTotal'].disable();
                        _controller.formItem.controls['total'].disable();
                        _controller.formItem.controls['unit'].disable();
                        if (_controller.editMod) {
                          _controller.formItem.controls['quantity'].disable();
                          _controller.formItem.controls['unitPrice'].disable();
                          _controller.formItem.controls['taxPercent'].disable();
                          _controller.formItem.controls['discountPercent'].disable();
                        }
                        _controller.user.roleInfos.forEach(role => {
                          if (role.name === 'ROLE_ADMIN') {
                            _controller.formItem.controls['quantity'].enable();
                            _controller.formItem.controls['unitPrice'].enable();
                            _controller.formItem.controls['taxPercent'].enable();
                            _controller.formItem.controls['discountPercent'].enable();
                          }
                        });
                        if (data.data.status === 'ORDER'
                          || data.data.status === 'PARTIAL'
                          || data.data.status === 'INVOICE') {
                            _controller.displayedColumns = ['itemUuid', 'quantity', 'unitPrice', 'taxPercent',
                            'taxTotal', 'discountPercent', 'total', 'approveQuantity', 'btn'];
                            _controller.form.controls['invoiceDate'].enable();
                            _controller.form.controls['invoiceNumber'].enable();
                            _controller.form.controls['invoiceNumber'].setValidators(Validators.required);
                            _controller.form.controls['invoiceDate'].setValidators(Validators.required);
                            _controller.formItem.controls['quantity'].disable();
                            _controller.formItem.controls['unitPrice'].disable();
                            _controller.formItem.controls['taxPercent'].disable();
                            _controller.formItem.controls['discountPercent'].disable();
                        }
                        if (data.data.status === 'INVOICE') {
                          _controller.form.controls['invoiceDate'].disable();
                          _controller.form.controls['invoiceNumber'].disable();
                          _controller.formItem.controls['approveQuantity'].disable();
                        }
                        if (data.data.status === 'ORDER') {
                          _controller.form.controls['invoiceDate'].enable();
                          _controller.form.controls['invoiceNumber'].enable();
                          _controller.formItem.controls['approveQuantity'].enable();
                        }
                        _controller.formItem.get('unitPrice').setValue('0');
                        _controller.formItem.get('uuid').setValue(item.uuid);
                        _controller.formItem.get('itemUuid').setValue(item.itemInfo.uuid);
                        _controller.formItem.get('storePartNumberUuid').setValue(item.itemInfo.uuid);
                        _controller.formItem.get('storePartNumber').setValue(item.itemInfo.storePartNumber);
                        _controller.formItem.get('barcode').setValue(item.barcode);
                        _controller.formItem.get('description').setValue(item.description);
                        _controller.formItem.get('quantity').setValue(item.quantity);
                        _controller.formItem.get('taxPercent').setValue(item.taxPercent);
                        _controller.formItem.get('unitPrice').setValue(item.unitPrice);
                        _controller.formItem.get('discountPercent').setValue(item.discountPercent);
                        _controller.formItem.get('discount').setValue(item.discount);
                        _controller.formItem.get('total').setValue(item.total);
                        _controller.formItem.get('taxTotal').setValue(item.taxTotal);
                        _controller.formItem.get('unit').setValue(item.unit);
                        _controller.formItem.get('approveQuantity').setValue(item.approveQuantity);
                        _controller.rows.push(_controller.formItem);
                        _controller.dataSourceOrderItem.next(_controller.rows.controls);
                      });
                    });

                  }, error => {
                    _controller.toastr.errorToastr(error.text, 'Error');
                  });
                }
            } else {
            }
        });

  }
  public onHistory(orderItem: any): void {
          $('#historyPartial').modal('show');

    this.orderService.findOrderItemDetail(orderItem.controls['uuid'].value).subscribe(data => {


      this.orderItemDetailListItemProjection.data = data.data.content;
    });

  }

  public itemSelected(fieldName: string): void {
    this.et_fields.forEach(item => {
      if (item.storePartNumber === fieldName) {
        this.addItemFrm.get('storePartNumberUuid').setValue(item.uuid);
        this.addItemFrm.get('barcode').setValue(item.barcode);
        this.addItemFrm.get('storePartNumber').setValue(item.storePartNumber);
        this.addItemFrm.get('description').setValue(item.itemDescription);
        this.addItemFrm.get('unit').setValue(item.unit);
      }
    });
  }
  public captureScreen(): void {
    this.router.navigateByUrl('/warehouse/purchase-pdf/' + this.order.uuid);
  }

  public serverdata = (event?: PageEvent) => {

    if (event.pageIndex === this.pageIndex + 1) {
      this.lowValue = this.lowValue + this.pageSizeMessage;
      this.highValue = this.highValue + this.pageSizeMessage;
    } else if (event.pageIndex === this.pageIndex - 1) {
      this.lowValue = this.lowValue - this.pageSizeMessage;
      this.highValue = this.highValue - this.pageSizeMessage;
    } else {

    }
    this.pageIndex = event.pageIndex;
    this.pageList(event.pageIndex, event.pageSize, false, this.selectedMessageOrder);
    return event;
  }

  public pageList = (page: number, size: number, first: boolean, orderItemUuid: string) => {

    this.page = new Pageable();
    this.page.page = page;
    this.page.size = size;
    this.orderService.orderItemMessageList(this.page, orderItemUuid).subscribe(data => {


      this.orderItemMessages.data = data.data.content;
      this.orderItemMessages.paginator = this.paginatorSubs;
      this.orderItemMessages.sort = this.sortSubs;
      this.totalElement = data.data.totalElements;
      if (first) {
        this.dataSource.paginator = this.paginatorSubs;
        this.dataSource.sort = this.sortSubs;

      }
      this.totalElement = data.data.totalElements;
    });

  }
  public displayFn(item?: string): string {
    return item;
  }
}
