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
import { TransferCreateUpdateForm } from 'src/app/models/transfer/transfer-create-form';
import { TransferItemCreateUpdateForm } from 'src/app/models/transfer/transfer-item-create';
import { TransferService } from 'src/app/services/warehouse/transfer.service';

declare const $: any;


@Component({
  selector: 'app-transfer-form',
  templateUrl: './transfer-form.component.html',
  styleUrls: ['./transfer-form.component.css']
})
export class TransferFormComponent implements OnInit {

  public user: User;
  public form: FormGroup;
  public commentForm: FormGroup;
  public formItem: FormGroup;
  public operationLock: boolean;
  public displayedColumns = ['itemUuid', 'quantity', 'space1', 'unit', 'space2', 'btn'];
  public dataSource = new MatTableDataSource<Order>();
  public dataSourceOrderItem = new BehaviorSubject<AbstractControl[]>([]);

  public supplierDataSource = new MatTableDataSource<SupplierCreateUpdateForm>();
  public siteDataSource = new MatTableDataSource<SiteForm>();
  public vehicleTypeList: VehicleTypeCreateUpdateForm[];
  public rows: FormArray;
  public et_fields: PartItem[];
  public page: Pageable;
  public pageMessage: Pageable;
  public transfer: TransferCreateUpdateForm;
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
  public approveQuantityItem: ApproveQuantityItem;
  public showPdfDiv: boolean;
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;
  public lowValue: number;
  public highValue: number;
  public selectedMessageOrder: string;



  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;
  public displayedColumnsMessage = ['message'];
  @ViewChild('paginatorSubsMsg') public paginatorSubsMsg: MatPaginator;
  @ViewChild('sortSubsMsg') public sortSubsMsg: MatSort;


  constructor(private readonly formBuilder: FormBuilder,
    private readonly activatedRouter: ActivatedRoute,
    private readonly supplierService: SupplierService,
    private readonly siteService: SiteService,
    private readonly transferService: TransferService,
    private readonly datePipe: DatePipe,
    private readonly vehicleTypeService: VehicleTypeService,
    private readonly itemService: ItemService,
    private readonly authenticationService: AuthenticationService,
    private readonly router: Router,
    public toastr: ToastrManager
  ) {
    this.user = this.authenticationService.currentUserValue;
    this.rows = this.formBuilder.array([]);
    this.create = true;
    this.createRegisterForm();
    this.transfer = new TransferCreateUpdateForm();
    this.orderItem = new OrderItem();
    this.orderStatus = new OrderStatus();
    this.orderStatus.status = 'REQUEST';
    this.pageIndex = 0;
    this.pageSize = 2;


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
    this.itemService.list(this.page).subscribe(data => {
      this.items.data = data.data.content;
      this.itemsAuto = data.data.content;
    });
    this.editMod = false;
    this.showPdfDiv = false;

  }
  public filterStates = (item: any) => {
    return this.items.data.filter(state =>
      state.storePartNumber.toLowerCase().indexOf(item.toLowerCase()) === 0);
  }
  public createRegisterForm(): void {
    this.form = this.formBuilder.group({
      uuid: [null],
      targetSite: [null, Validators.required],
      sourceSite: [null, Validators.required],
      transferNumber: [null],
      transferDate: [null],
      status: [null],
      controls: this.rows

    });
    this.form.controls['sourceSite'].setValue(this.authenticationService.currentUserValue.currentSite.uuid);

    this.commentForm = this.formBuilder.group({
      orderItemUuid: [null],
      message: [null],
      userName: [null],
    });
    this.form.controls['sourceSite'].disable();
    this.form.controls['transferNumber'].disable();
    this.form.controls['transferDate'].disable();
    this.form.controls['status'].disable();

    this.addItemFrm = this.formBuilder.group({
      itemUuid: new FormControl(null),
      storePartNumber: new FormControl(null, Validators.required),
      storePartNumber1: new FormControl(null),
      storePartNumberUuid: new FormControl(null, Validators.required),
      availableQuantity: new FormControl(null, Validators.required),
      barcode: new FormControl(null, Validators.required),
      description: new FormControl(null, Validators.required),
      quantity: new FormControl(null, Validators.required),
      unit: new FormControl(null),
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
        this.transferService.find(params.get('id')).subscribe(data => {
          this.form.patchValue(data.data);
          this.form.controls['transferDate'].setValue(
            this.datePipe.transform(data.data.transferDate, 'yyyy-MM-dd'));
          this.transfer = data.data;
          this.orderStatus.status = data.data.status;
          this.form.controls['targetSite'].setValue(data.data.targetSiteInfo.uuid);
          this.form.controls['sourceSite'].setValue(data.data.sourceSiteInfo.uuid);
          this.form.controls['status'].setValue(data.data.status);
          this.form.controls['uuid'].setValue(data.data.uuid);
          this.form.controls['targetSite'].disable();
          this.form.controls['sourceSite'].disable();
          data.data.transferItemInfos.forEach(item => {
            this.formItem = new FormGroup({
              uuid: new FormControl(null),
              itemUuid: new FormControl(null),
              storePartNumber: new FormControl(null, Validators.required),
              storePartNumber1: new FormControl(null),
              storePartNumberUuid: new FormControl(null),
              barcode: new FormControl(null, Validators.required),
              description: new FormControl(null, Validators.required),
              quantity: new FormControl(null, Validators.min(1)),
              unit: new FormControl(null),
              approveQuantity: new FormControl(0, [Validators.max(item.quantity)])
            }, { updateOn: 'change' });

            this.formItem.controls['barcode'].disable();
            this.formItem.controls['description'].disable();
            this.formItem.controls['unit'].disable();
            this.formItem.controls['storePartNumber'].disable();
            if (this.editMod && data.data.status !== 'REQUEST' ) {
              this.formItem.controls['quantity'].disable();
            }
            this.formItem.get('uuid').setValue(item.uuid);
            this.formItem.get('unit').setValue(item.itemInfo.unit);
            this.formItem.get('itemUuid').setValue(item.itemInfo.uuid);

            this.formItem.get('storePartNumberUuid').setValue(item.itemInfo.uuid);
            this.formItem.get('storePartNumber').setValue(item.itemInfo.storePartNumber);

            this.formItem.get('barcode').setValue(item.itemInfo.barcode);
            this.formItem.get('description').setValue(item.itemInfo.itemDescription);
            this.formItem.get('quantity').setValue(item.quantity);
            this.formItem.get('approveQuantity').setValue(item.approveQuantity);
            if (this.orderStatus.status === 'TRANSFER' ||
                                      this.orderStatus.status === 'DELIVER') {
              this.formItem.controls['approveQuantity'].disable();
            } else if (this.orderStatus.status === 'TRANSFER'
                    || this.orderStatus.status === 'REQUEST') {
              this.displayedColumns = ['itemUuid', 'quantity', 'space1', 'unit', 'space2',  'btn'];
            } else if (this.orderStatus.status === 'TRANSFER' || this.orderStatus.status === 'PARTIAL'
            || this.orderStatus.status === 'WAITING_DELIVER') {
              this.displayedColumns =
                  ['itemUuid', 'quantity', 'space1', 'unit', 'space2', 'btn'];
            }
            this.rows.push(this.formItem);
            this.dataSourceOrderItem.next(this.rows.controls);
          });
        });
      } else {
        this.form.get('status').setValue('REQUEST');
        // this.addOrderItem();
      }
    });
    this.addItemFrm.valueChanges
      .pipe(
        debounceTime(1500),
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
    this.transfer.uuid = this.form.controls['uuid'].value;
    this.transfer.sourceSiteUuid = this.form.controls['sourceSite'].value;
    this.transfer.targetSiteUuid = this.form.controls['targetSite'].value;
    this.transfer.status = this.form.controls['status'].value;
    this.transfer.transferNumber = this.form.controls['transferNumber'].value;
    /*
    this.rows.controls.forEach(frm => {
      this.order.totalQuantity = this.order.totalQuantity + frm.get('quantity').value;
    });
    */
    const tht = this;
    if (this.form.controls['uuid'].value === null) {
      this.transferService.save(this.transfer).subscribe(data => {
        this.transfer = data.data;
        for (let i = 0; i < this.rows.length; i++) {
          const transferItemCreateUpdateForm = new TransferItemCreateUpdateForm();
          transferItemCreateUpdateForm.transferUuid = data.data.uuid;
          transferItemCreateUpdateForm.itemUuid = tht.rows.at(i).get('storePartNumberUuid').value;
          transferItemCreateUpdateForm.quantity = tht.rows.at(i).get('quantity').value;
          transferItemCreateUpdateForm.approveQuantity = tht.rows.at(i).get('approveQuantity').value;
          tht.transferService.createTransferItem(transferItemCreateUpdateForm).subscribe(itm => {

          });
        }
        this.toastr.successToastr(data.type, data.text);
        this.router.navigateByUrl('/warehouse/transfers');
      }, error => {
        this.toastr.errorToastr('ERROR', 'Error');
      });
    } else {
      this.transferService.update(this.transfer).subscribe(data => {
        this.transfer = data.data;
        for (let i = 0; i < this.rows.length; i++) {
          const transferItemCreateUpdateForm = new TransferItemCreateUpdateForm();
          transferItemCreateUpdateForm.uuid = tht.rows.at(i).get('uuid').value;
          transferItemCreateUpdateForm.transferUuid = data.data.uuid;
          transferItemCreateUpdateForm.itemUuid = tht.rows.at(i).get('storePartNumberUuid').value;
          transferItemCreateUpdateForm.quantity = tht.rows.at(i).get('quantity').value;
          transferItemCreateUpdateForm.approveQuantity = tht.rows.at(i).get('approveQuantity').value;
          tht.transferService.createTransferItem(transferItemCreateUpdateForm).subscribe(itm => {
          });
        }
        this.toastr.successToastr(data.type, data.text);
        this.router.navigateByUrl('/warehouse/transfers');
      }, error => {
        this.toastr.errorToastr('ERROR', 'Error');
      });
    }
  }
  public onBack(): void {

    this.router.navigateByUrl('/warehouse/transfers');
  }

  public onChange = ($event, index: number) => {
    const tht = this;
    this.items.data.forEach(item => {
      if (item.uuid === $event.value) {
        tht.addItemFrm.controls['barcode'].setValue(item.barcode);
        tht.addItemFrm.controls['description'].setValue(item.itemDescription);
        tht.addItemFrm.controls['storePartNumber'].setValue(item.storePartNumber);
      }
    });
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
      unit: new FormControl(0, Validators.required),
      approveQuantity: new FormControl(0)
    }, { updateOn: 'change' }));
    this.rows.controls['barcode'].disable();
    this.rows.controls['unit'].disable();
    this.rows.controls['description'].disable();
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
          _cont.transferService.deleteTransferItem(row.value).subscribe(data => {
            _cont.rows.removeAt(index);
            _cont.dataSourceOrderItem.next(_cont.rows.controls);
          });
        }
      }
    }).catch(swal.noop);
  }
  public onAdd(): void {
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
      availableQuantity: new FormControl(null, Validators.required),
      quantity: new FormControl(null, Validators.min(1)),
      unit: new FormControl(null),
      approveQuantity: new FormControl(0)
    }, { updateOn: 'change' });
    this.formItem.controls['barcode'].disable();
    this.formItem.controls['unit'].disable();
    this.formItem.controls['description'].disable();
    this.formItem.controls['availableQuantity'].disable();

    this.orderItemTmp = new OrderItem();
    this.orderItemTmp = this.addItemFrm.value;

    this.formItem.get('itemUuid').setValue(this.addItemFrm.get('itemUuid').value);
    this.et_fields.forEach(item => {
      if (item.storePartNumber === this.addItemFrm.get('storePartNumber').value) {
        this.formItem.get('storePartNumberUuid').setValue(item.uuid);
        this.formItem.get('barcode').setValue(item.barcode);
        this.formItem.get('description').setValue(item.itemDescription);
        this.formItem.get('unit').setValue(item.unit);
      }
    });
    this.formItem.get('storePartNumber').setValue(this.addItemFrm.get('storePartNumber').value);
    this.formItem.get('quantity').setValue(this.addItemFrm.get('quantity').value);
    this.formItem.get('unit').setValue(this.addItemFrm.get('unit').value);
    let isRow = true;
    const tht = this;
    this.rows.controls.forEach((ss, index) => {
      if (ss.value.storePartNumberUuid === tht.formItem.get('storePartNumberUuid').value) {
        ss.get('quantity').setValue(ss.get('quantity').value + tht.formItem.get('quantity').value);
        isRow = false;
      }
    });
    if (isRow) {
      this.rows.push(this.formItem);
      this.dataSourceOrderItem.next(this.rows.controls);

    }
    for (let i = 0; i < this.rows.length; i++) {
      const transferItemCreateUpdateForm = new TransferItemCreateUpdateForm();
      transferItemCreateUpdateForm.uuid =  tht.rows.at(i).get('uuid').value;
      transferItemCreateUpdateForm.transferUuid = this.transfer.uuid;
      transferItemCreateUpdateForm.itemUuid = tht.rows.at(i).get('storePartNumberUuid').value;
      transferItemCreateUpdateForm.quantity = tht.rows.at(i).get('quantity').value;
      transferItemCreateUpdateForm.approveQuantity = tht.rows.at(i).get('approveQuantity').value;
      tht.transferService.createTransferItem(transferItemCreateUpdateForm).subscribe(itm => {

      });
    }

    this.addItemFrm.reset();
  }

  public itemFilter(fieldName: string): void {
    this.et_fields.forEach(item => {
      if (item.storePartNumber === fieldName) {
        this.addItemFrm.get('storePartNumberUuid').setValue(item.uuid);
        this.addItemFrm.get('barcode').setValue(item.barcode);
        this.addItemFrm.get('description').setValue(item.itemDescription);
        this.addItemFrm.get('unit').setValue(item.unit);
      }
    });
  }
  public newItem(): void {
    this.addItemFrm.reset();
    this.addItemFrm.controls['barcode'].disable();
    this.addItemFrm.controls['unit'].disable();
    this.addItemFrm.controls['description'].disable();
    this.addItemFrm.controls['storePartNumber'].enable();

  }

  public approveDialog = (status: string) => {
    const tht = this;
    if (status === 'WAITING_DELIVER') {
      if (this.rows.length === 0) {
        this.toastr.errorToastr('ERROR', 'Please Add item');
        return;
      }
    }
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
        tht.orderStatus.transferUuid = tht.form.value.uuid;
        tht.orderStatus.rejectionReason = '';
        tht.orderStatus.status = status;
        tht.transferService.approveReject(tht.orderStatus).subscribe(data => {
          tht.onBack();
        }, error => {
          this.toastr.errorToastr('', error);

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
        tht.orderStatus.transferUuid = tht.form.value.uuid;
        tht.orderStatus.rejectionReason = inputElement.value;
        tht.orderStatus.status = 'REJECTED';
        tht.transferService.approveReject(tht.orderStatus).subscribe(data => {
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
  }
  public onComment(index: number, row: any): void {
    $('#commentItem').modal('show');
    this.commentForm.get('orderItemUuid').setValue(row.controls.uuid.value);
    this.pageMessage = new Pageable();
    this.pageMessage.page = 0;
    this.pageMessage.size = 10;
    this.orderItemMessages.data = [];
    this.selectedMessageOrder = row.controls.uuid.value;

    this.pageList(this.pageIndex, this.pageSize, true, this.selectedMessageOrder);

    /*
    this.orderService.orderItemMessageList(this.pageMessage,.subscribe(data => {
      this.orderItemMessages.data = data.data.content;
      this.orderItemMessages.paginator = this.paginatorSubs;
      this.orderItemMessages.sort = this.sortSubs;
      this.totalElement = data.data.totalElements;


    });,
    */

  }
  public checkPermission(btn: string, obj: any): boolean {
    const status = this.orderStatus.status;
    if (btn === 'BTN_TRANSFER') {
      if (status === 'REQUEST' && this.editMod) {
        return true;
      }

    } else if (btn === 'BTN_DELIVER' && status === 'WAITING_DELIVER') {
      if (this.authenticationService.currentUserValue.currentSite.uuid
                                      === this.transfer.targetSiteInfo.uuid) {
         return true;
      } else {
        return false;
      }
    } else if (btn === 'BTN_REJECT' && status === 'WAITING_DELIVER') {
      if (this.authenticationService.currentUserValue.currentSite.uuid
                                          === this.transfer.targetSiteInfo.uuid) {
        return true;
     } else {
       return false;
     }
    } else if (btn === 'BTN_REFRESH' && (status === 'WAITING_DELIVER'
                                            || status === 'PARTIAL'
                                                  || status === 'REQUEST')) {

      if (this.authenticationService.currentUserValue.currentSite.uuid
                                            === this.transfer.targetSiteInfo.uuid) {
          return true;
      } else {
          return false;
      }

    } else if (btn === 'BTN_SAVE' && (status === 'REQUEST'
      || status === '')) {
        if (this.editMod) {
          return false;
        }
       return true;

    } else if (btn === 'BTN_DELETE' && (status === 'REQUEST'
      || status === '')) {
      return true;

    } else if (btn === 'BTN_NEW' && (status === 'REQUEST'
                                    || status === '')) {
      return true;

    } else if (btn === 'BTN_MESSAGE') {
      if (obj.value.uuid === null) {
        return false;
      }
      if (status === 'REQUEST') {
        return true;
      }
      if (!this.editMod) {
        return true;
      }
    } else {
      return false;
    }
  }
  public onCreateApproveQuantity(orderItem: any): void {
    if (!this.rows.valid) {
      return;
    }
    this.approveQuantityItem = new ApproveQuantityItem();
    this.approveQuantityItem.approveQuantity = orderItem.controls.approveQuantity.value;
    this.approveQuantityItem.transferItemUuid = orderItem.controls.uuid.value;
    this.transferService.createApproveQuantity(this.approveQuantityItem).subscribe(data => {
      this.orderStatus.status = data.data;
      this.form.controls['status'].setValue(data.data);
      this.toastr.successToastr('Approve Quantity updated', 'Success');
    });
  }

  public itemSelected(fieldName: string): void {
    this.et_fields.forEach(item => {
      if (item.storePartNumber === fieldName) {
        this.addItemFrm.get('storePartNumberUuid').setValue(item.uuid);
        this.addItemFrm.get('storePartNumber1').setValue(item.storePartNumber);
        this.addItemFrm.get('barcode').setValue(item.barcode);
        this.addItemFrm.get('description').setValue(item.itemDescription);
        this.addItemFrm.get('unit').setValue(item.unit);
        this.addItemFrm.get('availableQuantity').disable();
        this.addItemFrm.get('storePartNumber1').disable();
        this.transferService.findAvailableQuantity(item.uuid,
                this.authenticationService.currentUserValue.currentSite.uuid).subscribe(cal => {
                  this.addItemFrm.get('availableQuantity').setValue(cal.data);
                  this.addItemFrm.controls['quantity'].setValidators([Validators.max(cal.data)]);

        });
      }
    });
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
    this.pageList(event.pageIndex, event.pageSize, false, this.selectedMessageOrder);
    return event;
  }

  public pageList = (page: number, size: number, first: boolean, orderItemUuid: string) => {

    this.page = new Pageable();
    this.page.page = page;
    this.page.size = size;
  }
  public displayFn(item?: string): string {
    return item;
  }
}
