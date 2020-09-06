import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { FormBuilder, FormGroup, FormControl, FormArray, Validators, AbstractControl } from '@angular/forms';
import { PartItem } from 'src/app/models/item/item';
import { ItemService } from 'src/app/services/item/item.service';
import { debounceTime, tap, switchMap, finalize } from 'rxjs/operators';
import { BreakDownService } from 'src/app/services/workshop/break-down.service';
import { JobCardHistoryCreateForm } from 'src/app/models/workshop/break-down/job-create-desc';
import { ActivatedRoute, Router } from '@angular/router';
import { Pageable } from 'src/app/models/map/pagination';
import { MatPaginator, MatSort, MatTableDataSource, PageEvent } from '@angular/material';
import { JobCardItemCreateForm } from 'src/app/models/workshop/break-down/job-create-item';
import { JobCardInfo } from 'src/app/models/workshop/job-card/job-card-info';
import { JobCardInfoItem } from 'src/app/models/workshop/job-card/job-card-info-item';
import { JobCardUpdateForm } from 'src/app/models/workshop/job-card/job-update';
import { ToastrManager } from 'ng6-toastr-notifications';
import { UserService } from 'src/app/services/general/user.service';
import { User } from 'src/app/models/general/user';
import { UserForm } from 'src/app/models/users/userForm';
import { JobCardItemDeliveredListInfo } from 'src/app/models/request/item-delivered-info';
import swal from 'sweetalert2';
import { Location } from '@angular/common';
import { ItemInfo, ItemImage } from 'src/app/models/item/item-info';
import { Image } from 'src/app/models/general/image';

declare const $: any;

@Component({
  selector: 'app-job-card',
  templateUrl: './job-card.component.html',
  styleUrls: ['./job-card.component.scss', './job-card.component.css']
})
export class JobCardComponent implements OnInit {

  public form: FormGroup;
  public formJobDesc: FormGroup;
  public deliverfrm: FormGroup;
  public itemForm: FormGroup;
  public itemFormSelected: FormGroup;
  public operationLock: boolean;
  public dataSource = new MatTableDataSource<JobCardHistoryCreateForm>();
  public jobdataSource = new MatTableDataSource<JobCardInfoItem>();
  public displayedColumns = ['createdAt', 'creator', 'description'];
  public image: Image;


  public displayedColumnsDetail = ['deliveredDate', 'deliveredBy', 'receiveBy', 'quantity'];
  public jobDisplayedColumns = ['requestDate', 'requestUser', 'status', 'partNumber', 'description',
  'deliveryTime', 'availableQty',  'currentQuantity', 'Qty', 'btn'];
  public page: Pageable;
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;
  public pageDesc: Pageable;
  public totalElementDesc: number;
  public pageIndexDesc: number;
  public pageSizeDesc: number;
  public lowValue: number;
  public highValue: number;
  public onLoading: boolean;
  public rows: FormArray;
  public isLoading = false;
  public items = new MatTableDataSource<PartItem>();
  public et_fields: PartItem[];
  public jbCardArray: JobCardItemCreateForm[];
  public operatorList: UserForm[];
  public mechanicList: UserForm[];
  public foremanList: UserForm[];
  public itemsAuto = [];
  public jobCardInfo: JobCardInfo;
  public pageDetail: Pageable;
  public dataSourceDeliveredInfos = new MatTableDataSource<JobCardItemDeliveredListInfo>();
  public pageSizeDeliver: number;
  public jobCardInfoItem: JobCardInfoItem;
  public totalElementDeliver: number;
  public itemInfo: JobCardInfoItem;

  public currentIndex: number;
  public speed = 5000;
  public infinite = true;
  public direction = 'right';
  public directionToggle = true;
  public autoplay = true;
  public carouselShow: boolean;
  public imgInfo: ItemImage;
  public avatars = [];


  @Input() public pageSizeOptions: number[] = [5, 10, 25, 50, 100];
  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;

  @ViewChild('paginatorSubs') public paginatorSubsItem: MatPaginator;
  @ViewChild('sortSubs') public sortSubsItem: MatSort;

  constructor(public readonly formBuilder: FormBuilder,
    private readonly itemService: ItemService,
    private readonly activatedRouter: ActivatedRoute,
    private readonly userService: UserService,
    private readonly router: Router,
    public toastr: ToastrManager,
    private readonly breakDownService: BreakDownService,
    private readonly location: Location) {
    this.pageIndexDesc = 0;
    this.pageSizeDesc = 5;
    this.pageIndex = 0;
    this.pageSize = 5;
    this.pageSizeDeliver = 5;

    this.image = new Image();
    this.image.downloadUrl = './assets/img/placeholder.jpg';
    this.currentIndex = 0;


    this.jobCardInfo = new JobCardInfo();
    this.form = formBuilder.group({
      requestNumber: new FormControl(null),
      fleetNumber: new FormControl(null),
      jobCardStartDate: [null],
      kmHour: [null],
      currentMachineHours: [null],
      jobCardEndDate: [null],
      KmHour: [null],
      description: [null],
      vehicle: [null],
      operatorUser: [null, [Validators.required]],
      mechanicUser: [null, [Validators.required]],
      foremanUser: [null, [Validators.required]],
      jobCardStatus: [null],
      vehicleTypeName: [null],
      riskAssessment: new FormControl(false, [Validators.required]),
      lockOutProcedure: new FormControl(false, [Validators.required]),
      wheelNuts: new FormControl(false, [Validators.required]),
      oilLevel: new FormControl(false, [Validators.required]),
      machineGrease: new FormControl(false, [Validators.required]),
    });
    this.formJobDesc = formBuilder.group({
      description: new FormControl(null, [Validators.required]),
    });
    this.itemForm = formBuilder.group({
      storePartNumber: new FormControl(null, [Validators.required]),
      storePartNumberContent: new FormControl(null),
      storePartNumberUuid: new FormControl(null),
      unit: new FormControl(null),
      description: new FormControl(null),
      barcode: new FormControl(null),
      desc: new FormControl(null),
      quantity: new FormControl(null, [Validators.required])
    });
    this.itemFormSelected = formBuilder.group({
      storePartNumber: new FormControl(null, [Validators.required]),
      barcode: new FormControl(null),
      itemDescription: new FormControl(null),
      price: new FormControl(null),
      unit: new FormControl(null),
      currentQuantity: new FormControl(null, [Validators.required]),
      minStockQuantity: new FormControl(null, [Validators.required])
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

    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = 9999;
    this.userService.userGroup(this.page, 'OPERATOR').subscribe(data => {
      this.operatorList = data.data.content;
    });
    this.userService.userGroup(this.page, '*').subscribe(data => {
      this.mechanicList = data.data.content;
    });
    this.userService.userGroup(this.page, 'FOREMAN').subscribe(data => {
      this.foremanList = data.data.content;
    });

  }

  public ngOnInit(): void {
    this.activatedRouter.paramMap.subscribe(params => {
      this.jobCardInfo.uuid = params.get('id');
      this.pageList(this.pageIndexDesc, this.pageSizeDesc, true);
      this.pageListItem(this.pageIndex, this.pageSize, true);

      this.breakDownService.findJobCard(params.get('id')).subscribe(data => {
        this.jobCardInfo = data.data;
        this.form.patchValue(data.data);
        this.form.controls['operatorUser'].
          setValue(data.data.operatorUser.uuid);
        if (data.data.mechanicUser !== null) {
          this.form.controls['mechanicUser'].
            setValue(data.data.mechanicUser.uuid);
        }
        if (data.data.foremanUser !== null) {
          this.form.controls['foremanUser'].
            setValue(data.data.foremanUser.uuid);
        }
        this.form.controls['currentMachineHours'].setValue(data.data.currentKmHour);
        this.form.controls['fleetNumber'].disable();
        this.form.controls['jobCardStartDate'].disable();
        this.form.controls['jobCardEndDate'].disable();
        this.form.controls['kmHour'].disable();
        this.form.controls['currentMachineHours'].disable();
        this.form.controls['vehicle'].disable();
        this.form.controls['jobCardStatus'].disable();
        this.form.controls['vehicleTypeName'].disable();
        this.form.controls['description'].disable();

      });
    });
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = 9;
    this.itemForm.valueChanges
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
      this.isLoading = false;
    }, error => {
      this.et_fields = [];
    });
      this.itemForm.controls['barcode'].disable();
      this.itemForm.controls['description'].disable();
  }
  public onPdf = () => {
    this.router.navigateByUrl('/workshop/job-card-pdf/' + this.jobCardInfo.uuid);

  }

  public newDesc(): void {
    this.formJobDesc.reset();

  }

  public newItem(): void {
    this.itemForm.reset();
    this.itemService.filterAuto('*').subscribe(data => {
      this.items.data = data.data.content;
      this.itemsAuto = data.data.content;
    });
  }
  public itemDetail(itemInfo: JobCardInfoItem): void {
    this.itemInfo = itemInfo;
    this.itemFormSelected.patchValue(itemInfo);

    this.itemFormSelected.controls['storePartNumber'].disable();
    this.itemFormSelected.controls['barcode'].disable();
    this.itemFormSelected.controls['currentQuantity'].disable();
    this.itemFormSelected.controls['unit'].disable();
    this.itemFormSelected.controls['minStockQuantity'].disable();
    this.itemFormSelected.controls['itemDescription'].disable();
    this.itemFormSelected.controls['price'].disable();

    this.itemService.findItemList(itemInfo.uuid).subscribe(data => {
      this.form.patchValue(data.data.partInfo);
      this.avatars = data.data.partInfo.imageInfos;
    });

    $('#selecteditemDetail').modal('show');

  }
  public itemSelected(fieldName: string): void {

    this.et_fields.forEach(item => {
      if (item.storePartNumber === fieldName) {
        this.itemForm.get('storePartNumberUuid').setValue(item.uuid);
        this.itemForm.get('barcode').setValue(item.barcode);
        this.itemForm.get('description').setValue(item.itemDescription);
        this.itemForm.get('storePartNumberContent').setValue(item.storePartNumber);
        this.itemForm.get('unit').setValue(item.unit);

        return;
      }
    });
    this.itemForm.controls['storePartNumberContent'].disable();
    this.itemForm.controls['unit'].disable();
    this.itemForm.controls['barcode'].disable();
    this.itemForm.controls['description'].disable();

  }
  public displayFn(item?: string): string {
    return item;
  }

  public onDescription(): void {
    if (!this.formJobDesc.valid) {
      this.toastr.errorToastr('', 'Required field');
      return;
    }
    const jobCardHistoryCreateForm = new JobCardHistoryCreateForm();
    jobCardHistoryCreateForm.description = this.formJobDesc.value.description;
    jobCardHistoryCreateForm.jobCardUuid = this.jobCardInfo.uuid;
    this.breakDownService.createJobCardHistory(jobCardHistoryCreateForm).subscribe(data => {
      $('#desc').modal('hide');
      this.formJobDesc.reset();
      this.pageList(this.pageIndexDesc, this.pageSizeDesc, false);
      this.toastr.successToastr('', 'Success');
    }, error => {
      this.toastr.errorToastr('', 'Error');
    });
  }
  public serverDataDesc(event?: PageEvent): any {
    if (event.pageIndex === this.pageIndexDesc + 1) {
      this.lowValue = this.lowValue + this.pageSizeDesc;
      this.highValue = this.highValue + this.pageSizeDesc;
    } else if (event.pageIndex === this.pageIndex - 1) {
      this.lowValue = this.lowValue - this.pageSizeDesc;
      this.highValue = this.highValue - this.pageSizeDesc;
    }
    this.pageIndexDesc = event.pageIndex;
    this.pageList(this.pageIndexDesc, this.pageSizeDesc, false);
    return event;
  }
  public pageList(page: number, size: number, first: boolean): void {
    this.pageDesc = new Pageable();
    this.pageDesc.page = page;
    this.pageDesc.size = size;
    this.breakDownService.findJobCardHistory(this.jobCardInfo.uuid, this.pageDesc).subscribe(data => {
      this.dataSource.data = data.data.content;
      if (first) {
        this.dataSource.paginator = this.paginatorSubs;
        this.dataSource.sort = this.sortSubs;
      }

      this.onLoading = true;
      this.totalElementDesc = data.data.totalElements;

    });
  }
  public onAddItem(): void {
    if (!this.itemForm.valid) {
      this.toastr.errorToastr('', 'Required field');
      return;
    }
    const jobCardItemCreateForm = new JobCardItemCreateForm();
    jobCardItemCreateForm.jobCardUuid = this.jobCardInfo.uuid;
    this.et_fields.forEach(item => {
      if (item.storePartNumber === this.itemForm.get('storePartNumber').value) {
        this.itemForm.get('storePartNumberUuid').setValue(item.uuid);
        this.itemForm.get('barcode').setValue(item.barcode);
        this.itemForm.get('unit').setValue(item.unit);
        this.itemForm.get('storePartNumber').setValue(item.storePartNumber);
        this.itemForm.get('description').setValue(item.itemDescription);
      }
    });
    jobCardItemCreateForm.itemUuid = this.itemForm.value.storePartNumberUuid;
    jobCardItemCreateForm.quantity = this.itemForm.value.quantity;
    jobCardItemCreateForm.description = this.itemForm.value.description;
    this.breakDownService.createJobCardItem(jobCardItemCreateForm).subscribe(data => {
      $('#item').modal('hide');
      this.itemForm.reset();
      this.pageListItem(this.pageIndex, this.pageSize, true);
      this.toastr.successToastr('', 'Success');
    }, error => {
      this.toastr.errorToastr('', 'Error');
    });

  }

  public pageListItem(page: number, size: number, first: boolean): void {
    this.page = new Pageable();
    this.page.page = page;
    this.page.size = size;
    this.breakDownService.findJobCardItem(this.jobCardInfo.uuid, this.page).subscribe(data => {
      this.jobdataSource.data = data.data.content;
      if (first) {
        this.jobdataSource.paginator = this.paginatorSubsItem;
        this.jobdataSource.sort = this.sortSubsItem;
      }

      this.onLoading = true;
      this.totalElement = data.data.totalElements;
    });
  }
  public serverDataItem(event?: PageEvent): any {
    if (event.pageIndex === this.pageIndex + 1) {
      this.lowValue = this.lowValue + this.pageSize;
      this.highValue = this.highValue + this.pageSize;
    } else if (event.pageIndex === this.pageIndex - 1) {
      this.lowValue = this.lowValue - this.pageSizeDesc;
      this.highValue = this.highValue - this.pageSizeDesc;
    }
    this.pageIndex = event.pageIndex;
    this.pageListItem(this.pageIndex, this.pageSize, false);
    return event;
  }
  public onUpdateJob(): void {
    const jobCardUpdateForm = new JobCardUpdateForm();

    jobCardUpdateForm.jobCardUuid = this.jobCardInfo.uuid;
    jobCardUpdateForm.foremanUserUuid = this.form.value.foremanUser;
    jobCardUpdateForm.operatorUserUuid = this.form.value.operatorUser;
    jobCardUpdateForm.mechanicUserUuid = this.form.value.mechanicUser;
    jobCardUpdateForm.wheelNuts = this.form.value.wheelNuts;
    jobCardUpdateForm.riskAssessment = this.form.value.riskAssessment;
    jobCardUpdateForm.oilLevel = this.form.value.oilLevel;
    jobCardUpdateForm.lockOutProcedure = this.form.value.lockOutProcedure;
    jobCardUpdateForm.machineGrease = this.form.value.machineGrease;
    this.breakDownService.jobCardUpdateForm(jobCardUpdateForm).subscribe(data => {
      this.toastr.successToastr('', 'Success');
      this.onBack();
    }, error => {
      this.toastr.errorToastr('', 'Error');
    });

  }
  public onCloseJob(): void {

    if (this.dataSource.data.length === 0) {
      this.toastr.warningToastr('', 'Please, enter at least one description');
      return;
    }
    if (!this.form.valid) {
      this.toastr.errorToastr('', 'Require field');
      return;

    }
    if (!this.form.value.riskAssessment) {
      this.toastr.infoToastr('', 'Please Check: Risk Assessment Completed and asistant informed ');
      return;
    }
    if (!this.form.value.lockOutProcedure) {
      this.toastr.infoToastr('', 'Please Check: Lock Out Procedure Done Correctly');
      return;
    }
    if (!this.form.value.wheelNuts) {
      this.toastr.infoToastr('', 'Please Check: Whell nuts');
      return;
    }
    if (!this.form.value.machineGrease) {
      this.toastr.infoToastr('', 'Please Check: Machine grease');
      return;
    }
    if (!this.form.value.oilLevel) {
      this.toastr.infoToastr('', 'Please Check: Oil Level/Oil Leaks');
      return;
    }
    const jobCardUpdateForm = new JobCardUpdateForm();

    jobCardUpdateForm.jobCardUuid = this.jobCardInfo.uuid;
    jobCardUpdateForm.foremanUserUuid = this.form.value.foremanUser;
    jobCardUpdateForm.operatorUserUuid = this.form.value.operatorUser;
    jobCardUpdateForm.mechanicUserUuid = this.form.value.mechanicUser;
    jobCardUpdateForm.wheelNuts = this.form.value.wheelNuts;
    jobCardUpdateForm.riskAssessment = this.form.value.riskAssessment;
    jobCardUpdateForm.oilLevel = this.form.value.oilLevel;
    jobCardUpdateForm.lockOutProcedure = this.form.value.lockOutProcedure;
    jobCardUpdateForm.machineGrease = this.form.value.machineGrease;
    this.breakDownService.jobCardUpdateForm(jobCardUpdateForm).subscribe(data => {
      this.breakDownService.closeJobCard(this.jobCardInfo.uuid).subscribe(item => {
        if (item.data) {
          this.toastr.successToastr('', 'Job Card closed');
          this.onBack();
        } else {
          this.toastr.errorToastr('', item.text);
        }
      }, err => {
        this.toastr.errorToastr('', 'Error');
      });
    }, error => {
      this.toastr.errorToastr('', 'Error');
    });

  }
  public onBack(): void {
    this.location.back();
  }

  public deliverDetail(element: JobCardInfoItem): void {
    this.jobCardInfoItem = element;
    $('#deliverDetail').modal('show');
    this.deliverfrm.reset();
    this.deliverfrm.get('stockCode').setValue(element.itemInfo.storePartNumber);
    this.deliverfrm.get('barcode').setValue(element.itemInfo.barcode);
    this.deliverfrm.get('description').setValue(element.itemInfo.itemDescription);
    this.deliverfrm.get('requestedQuantity').setValue(element.quantity);
    this.deliverfrm.get('remainingQuantity').setValue(element.remainingQuantity);
    this.deliverfrm.get('deliveredQuantity').setValue(element.deliveredQuantity);

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

  public deleteDeliver(item: JobCardInfoItem): void {
    const _controller = this;
    swal({
      title: 'Are you sure you want to delete this Item ?',
      html: '<div class=“form-group”>' +
        '</div>',
      showCancelButton: true,
      confirmButtonClass: 'btn btn-success',
      confirmButtonText: 'Yes',
      cancelButtonText: 'No',
      cancelButtonClass: 'btn btn-danger',
      buttonsStyling: false
    }).then((result) => {
      if (result.value) {
        _controller.breakDownService.deleteJobCardItem(item.uuid).subscribe(data => {
          if (data.data) {
            _controller.toastr.successToastr('', 'Job Card Item Deleted');
            _controller.pageListItem(_controller.pageIndex, _controller.pageSize, false);
          }
      });
    }
    });

  }
  public pageListDeliver(page: number, size: number, first: boolean): void {
    this.pageDetail = new Pageable();
    this.pageDetail.page = page;
    this.pageDetail.size = size;
    this.breakDownService.findJobCardItemDelivered(this.jobCardInfoItem.uuid, this.pageDetail).subscribe(list => {
      this.dataSourceDeliveredInfos.data = list.data.content;
      this.totalElementDeliver = list.data.totalElements;
      if (first) {
        this.dataSourceDeliveredInfos.paginator = this.paginatorSubs;
        this.dataSourceDeliveredInfos.sort = this.sortSubs;
      }
    });
  }
  public serverDataDeliver(event?: PageEvent): any {
    if (event.pageIndex === this.pageIndexDesc + 1) {
      this.lowValue = this.lowValue + this.pageSizeDeliver;
      this.highValue = this.highValue + this.pageSizeDeliver;
    } else if (event.pageIndex === this.pageIndex - 1) {
      this.lowValue = this.lowValue - this.pageSizeDeliver;
      this.highValue = this.highValue - this.pageSizeDeliver;
    }
    this.pageIndexDesc = event.pageIndex;
    this.pageListDeliver(this.pageIndexDesc, this.pageSizeDeliver, false);
    return event;
  }
}
