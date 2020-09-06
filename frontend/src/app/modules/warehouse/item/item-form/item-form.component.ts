import { Component, OnInit, AfterContentInit, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormArray, ValidatorFn, FormControl } from '@angular/forms';
import { Validators, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatTableDataSource, PageEvent } from '@angular/material';
import { Pageable } from 'src/app/models/map/pagination';
import { VehicleAttributeValue } from 'src/app/models/map/vehicle-attribute-value';
import { VehicleTypeAttributes, VehicleAttributeListDetailCreateForm } from 'src/app/models/map/pagination.type';
import { VehicleAttribute } from 'src/app/models/vehicle/vehicle-attribute';
import { ImageService } from 'src/app/services/general/image.service';
import { ToastrManager } from 'ng6-toastr-notifications';
import { Image } from 'src/app/models/general/image';
import { VehicleTypeCreateUpdateForm } from 'src/app/models/vehicle/vehicle-type-create-update-form';
import { VehicleTypeAttributeDto } from 'src/app/models/map/VehicleTypeAttributeDto';
import { ItemService } from 'src/app/services/item/item.service';
import { ItemTypeService } from 'src/app/services/item/item-type.service';
import { ItemInfo, ItemImage } from 'src/app/models/item/item-info';
import { MatDialog } from '@angular/material';
import { PropertyForm } from 'src/app/models/property/property-form';
import { PropertyService } from 'src/app/services/general/property.service';
import { BreakDownService } from 'src/app/services/workshop/break-down.service';
import { ItemPurchasehistory } from 'src/app/models/item/item-purchase-history';
import { DatePipe } from '@angular/common';
import { SelectionModel } from '@angular/cdk/collections';
import { debounceTime, tap, switchMap, finalize } from 'rxjs/operators';
import { NgxUiLoaderService } from 'ngx-ui-loader';

declare interface DataTable {
  dataRows: TableWithCheckboxes[];
}
declare interface TableWithCheckboxes {
  id?: number;
  name: string;
  value: string;
  list?: TableWithCheckboxes[];
}

declare const $: any;
@Component({
  selector: 'app-item-form',
  templateUrl: 'item-form.component.html',
  styleUrls: ['item-form.component.scss', 'item-form.component.css']
})

export class ItemFormComponent implements OnInit, AfterContentInit {

  public vehicleTypeList: VehicleTypeCreateUpdateForm[];
  public attributesPanel: boolean;
  public items: FormArray;
  public listAttr: any[];
  public selectAttr: string[];
  public form: FormGroup;
  public dateFrm: FormGroup;
  public purchaseFrm: FormGroup;
  public vehicleTypeAttributeDto: VehicleTypeAttributeDto[];
  public vehicleAttributes: VehicleAttribute[];
  public types: VehicleTypeAttributes;
  public vehicleAttributeValues: VehicleAttributeValue[];
  public fileData: File = null;
  public page: Pageable;
  public vehicleAttributeListDetailCreateForm: VehicleAttributeListDetailCreateForm;
  public operationLock: boolean;
  public showBarcode: boolean;
  public image: Image;
  public itemInfo: ItemInfo;
  public purchaseHistoryDataSource = new MatTableDataSource<ItemPurchasehistory>();
  public displayedColumns = [ 'purchaseDate', 'invoiceNummber', 'purchaseQuantity', 'unitPrice', 'supplier'];
  public selection = new SelectionModel<ItemPurchasehistory>(true, []);


  public elementType = 'svg';
  public value = 'someValue12340987';
  public format = 'CODE128';
  public lineColor = '#000000';
  public width = 1.4;
  public height = 40;
  public displayValue = true;
  public fontOptions = '';
  public font = 'monospace';
  public textAlign = 'center';
  public textPosition = 'bottom';
  public textMargin = 2;
  public fontSize = 12;
  public background = '#ffffff';
  public margin = 10;
  public marginTop = 1;
  public marginBottom = 1;
  public marginLeft = 70;
  public marginRight = 1;
  public propertys: PropertyForm[];
  @ViewChild('fileImage') public fileImage: ElementRef;


  get values(): string[] {
    if (this.value === null) {
      return [''];
    } else {
      return this.value.split('\n');

    }
  }
  public codeList: string[] = [
    '', 'CODE128',
    'CODE128A', 'CODE128B', 'CODE128C',
    'UPC', 'EAN8', 'EAN5', 'EAN2',
    'CODE39',
    'ITF14',
    'MSI', 'MSI10', 'MSI11', 'MSI1010', 'MSI1110',
    'pharmacode',
    'codabar'
  ];

  public pageDetail: Pageable;
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;
  public pageDesc: Pageable;
  public totalElementDesc: number;
  public pageIndexDesc: number;
  public pageSizeDesc: number;
  public isLoading: boolean;
  public lowValue: number;
  public highValue: number;

  public dataSource = new MatTableDataSource<VehicleTypeCreateUpdateForm>();
  @ViewChild('storePartNumber') public storePartNumber: ElementRef;
  public currentIndex: number;
  public speed = 5000;
  public infinite = true;
  public direction = 'right';
  public directionToggle = true;
  public autoplay = true;
  public carouselShow: boolean;
  public imgInfo: ItemImage;
  public avatars = [];

  constructor(private readonly formBuilder: FormBuilder,
    private readonly itemService: ItemService,
    private readonly itemTypeService: ItemTypeService,
    private readonly imageService: ImageService,
    private readonly breakDownService: BreakDownService,
    private readonly ngxService: NgxUiLoaderService,
    private readonly activatedRouter: ActivatedRoute,
    private readonly propertyService: PropertyService,
    private readonly router: Router,
    private readonly dialog: MatDialog,
    public toastr: ToastrManager,
    private readonly datePipe: DatePipe
  ) {
    this.createRegisterForm();
    this.image = new Image();
    this.image.downloadUrl = './assets/img/placeholder.jpg';
    this.currentIndex = 0;
    this.showBarcode = false;
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = 99;
    this.propertyService.findByGroupName(this.page, 'ItemsUnit').subscribe(data => {
      this.propertys = data.data.content;
    });

    this.dateFrm = formBuilder.group({
      startDate: [this.datePipe.transform(new Date() , 'yyyy-MM-dd'), [Validators.required]],
      endDate: [this.datePipe.transform(new Date() , 'yyyy-MM-dd'), [Validators.required]],
      reportStartTime: ['00:00'],
      reportCloseTime: ['23:59'],
      search: new FormControl()
    });
  }

  public ngAfterContentInit(): void {
    this.storePartNumber.nativeElement.focus();

  }

  public createRegisterForm(): void {
    this.form = this.formBuilder.group({
      uuid: [null],
      storePartNumber: [null, Validators.required],
      itemDescription: [null, Validators.required],
      minStockQuantity: [null, Validators.required],
      unit: [null, Validators.required],
      partTypeUuid: [null],
      partType: [null],
      imageUuid: [null],
      barcode: [null]
    });
    this.value = this.form.value.barcode;

  }

  public ngOnInit(): void {
    this.operationLock = false;
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = 1000;
    this.itemTypeService.list(this.page).subscribe(data => {

      this.dataSource.data = data.data.content;
      this.vehicleTypeList = data.data.content;
    });
    this.activatedRouter.paramMap.subscribe(params => {
      if (params.has('id')) {
        this.itemService.findItemList(params.get('id')).subscribe(data => {
          this.form.patchValue(data.data.partInfo);
          this.itemInfo = data.data;
          this.showBarcode = true;
          this.form.controls['partType'].setValue(data.data.partInfo.itemTypeInfo.name);
          this.onTypeChange(data.data.partInfo.itemTypeInfo.uuid);
          this.value = this.form.value.barcode;
          this.form.controls['partTypeUuid'].setValue(data.data.partInfo.itemTypeInfo.uuid);
          this.avatars = data.data.partInfo.imageInfos;
          data.data.partInfo.imageInfos.forEach(item => {
            if (item.selected) {
              this.image.downloadUrl = item.downloadUrl;
            }
          });
        });
      }
    });
    this.form.controls['partType'].valueChanges
      .pipe(
        debounceTime(100),
        tap(() => {
          this.dataSource.data = [];
          this.isLoading = true;
        }),
        switchMap(value => this.itemTypeService.filter(this.page, value ? value : '*')
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
  }

  public handleNumberChange = (val) => {
    const val_plus_one = parseInt(val, 10) + 1;
    try {
      this.form.removeControl(val_plus_one.toString());
    } catch { }
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
  public onTypeChange(uuid: string): void {
    this.attributesPanel = false;
    this.dataSource.data.forEach(item => {
      if (item.uuid === uuid) {
          this.form.get('partType').setValue(item.name);
          this.form.get('partTypeUuid').setValue(item.uuid);
          return;
      }
  });

    this.itemTypeService.find(uuid).subscribe(data => {
      this.types = data.data;
      const tht = this;
      if (this.itemInfo !== undefined) {
        this.itemInfo.partInfo.itemTypeInfo.vehicleTypeAttributes.forEach(item => {
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
  public onSubmit(): void {
    if (!this.form.valid) {


    } else {
      if (this.form.value.partTypeUuid !== null) {
        this.types.vehicleTypeUuid = this.form.value.partTypeUuid;
      }
      this.itemService.save(this.form.value).subscribe(data => {
        this.toast(data.type, data.text);
        this.form.patchValue(data);
        this.types.vehicleUuid = data.data.uuid;
        this.types.vehicleAttributeListDetailCreateForm = [];
        this.vehicleAttributeListDetailCreateForm = new VehicleAttributeListDetailCreateForm();
        this.types.vehicleTypeAttributes.forEach(element => {
          this.vehicleAttributeListDetailCreateForm = new VehicleAttributeListDetailCreateForm();
          this.vehicleAttributeListDetailCreateForm.vehicleAttributeUuid = element.uuid;
          this.vehicleAttributeListDetailCreateForm.vehicleAttributeValueUuid = element.selectedVehicleAttrUuid;
          this.types.vehicleAttributeListDetailCreateForm.push(this.vehicleAttributeListDetailCreateForm);
        });
        this.itemService.createItemList(this.types).subscribe(_data => {
        });
        this.imgInfo = new ItemImage();
        this.imgInfo.itemUuid = data.data.uuid;
        this.imgInfo.imageInfos = this.avatars;
        this.itemService.createImageVehicle(this.imgInfo).subscribe(img => {
        });
        this.itemService.createVehicleList(this.types).subscribe(_data => {
        });
        this.router.navigateByUrl('/warehouse/items');
      }, error => {
        this.operationLock = false;
      });

    }
  }
  public onFileChanged = (event) => {
    this.operationLock = true;
    this.fileData = <File>event.target.files[0];
    const mimeType = this.fileData.type;
    if (mimeType.match(/image\/*/) == null) {
      this.toastr.warningToastr('Only images are supported', 'File Type');
      this.image = new Image();
      this.image.downloadUrl =  './assets/img/placeholder.jpg';
      this.avatars.forEach(i => {
        if (i.selected) {
          this.image.downloadUrl = i.downloadUrl;
        }
      });
      this.fileImage.nativeElement.value = '';
      this.operationLock = false;

      return;

    }
    if (this.fileData.size > 1048576) {
      this.toastr.warningToastr('File size max: 1MB ', 'File Size');
      this.image = new Image();
      this.image.downloadUrl =  './assets/img/placeholder.jpg';
      this.fileImage.nativeElement.value = '';
      this.avatars.forEach(i => {
        if (i.selected) {
          this.image.downloadUrl = i.downloadUrl;
        }
      });
      this.operationLock = false;

      return;
    }
    this.ngxService.start();
    this.imageService.upload(this.fileData).subscribe(data => {
      this.ngxService.stop();
      this.image = new Image();
      this.image.uuid = data.data.uuid;
      this.image.downloadUrl = data.data.downloadUrl;
      if (this.avatars.length === 0) {
        this.image.selected = true;
        this.form.controls['imageUuid'].setValue(data.data.uuid);
      } else {
        this.image.selected = false;
      }
      this.avatars.push(this.image);
      this.operationLock = false;
    });
  }

  public onBack(): void {

    this.router.navigateByUrl('/warehouse/items');
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

  public onModal(): void {
    this.dialog.open(ItemFormComponent);
  }
  public displayErrorControl = (field: string) => {
    if (this.form.controls[field].value) {
      const s = '' + this.form.controls[field].value;
      if (s.length === 15) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
  public onImageDelete(uuid: string): void {
    const _controller = this;
    this.imageService.delete(uuid).subscribe(img => {
      this.avatars = this.avatars.filter(item => item.uuid !== uuid );
      this.toastr.successToastr('Image deleted', 'Success');
      this.avatars = this.avatars;
      if (this.avatars.length === 0) {
        _controller.image = new Image();
        _controller.image.downloadUrl = './assets/img/placeholder.jpg';
      }
    });
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
  public onImageDefault(img: Image): void {
    if (this.avatars.length === 0) {
      return;
    }
    this.avatars.forEach(i => {
      if (img.uuid === i.uuid) {
        i.selected = true;
      } else {
        i.selected = false;
      }
    });
    img.selected = true;
    this.image.downloadUrl = img.downloadUrl;
    this.imgInfo = new ItemImage();
    this.imgInfo.itemUuid = this.form.value.uuid;
    this.imgInfo.imageInfos = this.avatars;
    this.itemService.createImageVehicle(this.imgInfo).subscribe(itm => {
    });

  }
  public onBarcodeGenerator(): void {
    this.showBarcode = true;
    if (this.form.value.barcode !== '') {
      this.value = this.form.value.barcode;
    }

  }
  public printDiv(): void {
    $('#barcode').modal('hide');
    const print_div = document.getElementById('printThis');
    const print_area = window.open();
    print_area.document.write(print_div.innerHTML);
    print_area.document.close();
    print_area.focus();
    print_area.print();
    print_area.close();
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
    return event;
  }
    public checkboxLabel(row?: ItemPurchasehistory): string {
    if (!row) {
      return `${this.isAllSelected() ? 'select' : 'deselect'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.itemUuid + 1}`;
  }
  public isAllSelected = () => {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }
  public pageList(page: number, size: number, first: boolean): void {
    this.pageDesc = new Pageable();
    this.pageDesc.page = page;
    this.pageDesc.size = size;
  }

  public purchaseHistory(): void {
    const filter = new ItemPurchasehistory();
    filter.itemUuid = this.itemInfo.partInfo.uuid;
    filter.startDate = this.datePipe.transform(this.dateFrm.value.startDate, 'yyyy-MM-dd');
    filter.startDate = this.datePipe.transform(filter.startDate +
      ' ' + this.dateFrm.value.reportStartTime, 'yyyy-MM-dd HH:mm');

    filter.endDate = this.datePipe.transform(this.dateFrm.value.endDate, 'yyyy-MM-dd');
    filter.endDate = this.datePipe.transform(filter.endDate
      + ' ' + this.dateFrm.value.reportCloseTime, 'yyyy-MM-dd HH:mm');

    filter.page = this.page.page;
    filter.size = this.page.size;
    this.itemService.findAllItemOrder(filter).subscribe(data => {
      this.purchaseHistoryDataSource.data = data.data.content;

    });
  }
}
