import { Component, OnInit, Input, ElementRef, ViewChild } from '@angular/core';
import { SupplierService } from '../../../../services/warehouse/supplier.service';
import { Pageable } from '../../../../models/map/pagination';
import { FormArray, FormBuilder, FormGroup, ValidatorFn, Validators,
  AbstractControl, FormControl } from '@angular/forms';
import { MatTableDataSource } from '@angular/material';
import { ActivatedRoute, Router } from '@angular/router';
import swal from 'sweetalert2';
import { ToastrManager } from 'ng6-toastr-notifications';
import { SupplierCreateUpdateForm } from 'src/app/models/suppliers/supplier-create-update-form';
import { BehaviorSubject } from 'rxjs';
import { SupplierContact } from 'src/app/models/suppliers/supplier-contact';
import { VehicleImage } from 'src/app/models/vehicle/vehicle-info';
import { Image } from 'src/app/models/general/image';
import { ImageService } from 'src/app/services/general/image.service';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { SupplierImageCreateForm } from 'src/app/models/suppliers/supplier-image';
import { SupplierForm } from 'src/app/models/suppliers/supplier-form';

declare const $: any;

declare interface TableWithCheckboxes {
  id?: number;
  name: string;
  value: string;
  list?: TableWithCheckboxes[];
}

@Component({
  selector: 'app-supplier-form',
  templateUrl: './supplier-form.component.html',
  styleUrls: ['./supplier-form.component.css', './supplier-form.component.scss'],
  providers: [SupplierService]

})
export class SupplierFormComponent implements OnInit {
  public supplierName: string;
  public supplierAddress: string;
  public regNo: string;
  public taxNo: string;
  public pageIndex: number;
  public pageSize: number;
  public lowValue: number;
  public highValue: number;
  public fileData: File = null;
  public page: Pageable;
  public supplierForm: FormGroup;
  public operationLock: boolean;
  public dataSourceOrderItem = new BehaviorSubject<AbstractControl[]>([]);
  public displayedColumns = ['name', 'role', 'email', 'phoneNo', 'landLine', 'btn'];
  public rows: FormArray;
  public contact: SupplierContact;
  public form: FormGroup;
  public supplierFormEn: SupplierForm;



  public currentIndex: number;
  public speed = 5000;
  public infinite = true;
  public direction = 'right';
  public directionToggle = true;
  public autoplay = true;
  public carouselShow: boolean;
  public avatars = [];
  public image: Image;

  public supplierDto: SupplierCreateUpdateForm;
  public dataSource = new MatTableDataSource<SupplierCreateUpdateForm>();

  @Input() public pageSizeOptions: number[] = [5, 10, 25, 50, 100];
  @ViewChild('fileImage') public fileImage: ElementRef;
  @Input() public supplierUuid: string;
  @Input() public modeMode: boolean;

  constructor(private readonly formBuilder: FormBuilder,
    private readonly supplierService: SupplierService,
    private readonly activatedRouter: ActivatedRoute,
    private readonly ngxService: NgxUiLoaderService,
    private readonly imageService: ImageService,
    private readonly router: Router,
    public toastr: ToastrManager) {
    this.pageIndex = 0;
    this.pageSize = 5;
    this.lowValue = 0;
    this.highValue = 5;
    this.createRegisterForm();
    this.rows = this.formBuilder.array([]);
    this.contact = new SupplierContact();

    this.image = new Image();
    this.image.downloadUrl = './assets/img/placeholder.jpg';
  }
  public ngOnInit(): void {
    this.activatedRouter.paramMap.subscribe(params => {
      if (params.keys.length === 0) {
        return;
      }
      this.editForm(params.get('id'));
    });
    if (this.supplierUuid) {
      this.editForm(this.supplierUuid);
    }

    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = this.pageSize;
    this.supplierService.list(this.page).subscribe(data => {
      this.dataSource.data = data.data.content;

    });
  }
  public editForm(uuid: string): void {
    this.supplierService.find(uuid).subscribe(data => {
      this.supplierForm.patchValue(data.data);
      this.supplierFormEn = data.data;
      this.avatars = data.data.imageInfos;
      data.data.imageInfos.forEach(item => {
        if (item.selected) {
          this.image.downloadUrl = item.downloadUrl;
        }
      });
      data.data.contacts.forEach(item => {
        this.rows.push(new FormGroup({
          uuid: new FormControl(item.uuid),
          name: new FormControl(item.name, Validators.required),
          role: new FormControl({ value: item.role, disabled: false }, Validators.required),
          email: new FormControl({ value: item.email, disabled: false }, [Validators.required, Validators.email]),
          landLine: new FormControl({ value: item.landLine, disabled: false }),
          phoneNo: new FormControl({ value: item.phoneNo, disabled: false }, Validators.required)
        }, { updateOn: 'blur' }));
        this.dataSourceOrderItem.next(this.rows.controls);
      });
    });

  }
  public createRegisterForm(): void {
    this.supplierForm = this.formBuilder.group({
      uuid: [null],
      name: [null, Validators.required],
      address: [null, Validators.required],
      registerNumber: [null, Validators.required],
      taxNumber: [null, Validators.required],
      contacts: [null, Validators.required],
      imageUuid: [null],
      controls: this.rows
    });
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

  public onSubmit(): void {
    if (!this.rows.valid) {
      this.toastr.errorToastr('Error', 'Invalid Data');
      return;
    }
    this.operationLock = true;
    this.supplierDto = new SupplierCreateUpdateForm();
    this.supplierDto.uuid = this.supplierForm.value.uuid;
    this.supplierDto.name = this.supplierForm.value.name;
    this.supplierDto.address = this.supplierForm.value.address;
    this.supplierDto.taxNumber = this.supplierForm.value.taxNumber;
    this.supplierDto.registerNumber = this.supplierForm.value.registerNumber;
    this.supplierService.create(this.supplierDto).subscribe(data => {
      this.operationLock = false;
      this.toast(data.type, data.text);
      this.rows.controls.forEach(frm => {
        this.contact = new SupplierContact();
        this.contact.supplierUuid = data.data.uuid;
        this.contact.uuid = frm.value.uuid;
        this.contact.name = frm.value.name;
        this.contact.role = frm.value.role;
        this.contact.email = frm.value.email;
        this.contact.landLine = frm.value.landLine;
        this.contact.phoneNo = frm.value.phoneNo;
        this.supplierService.createSupplierContact(this.contact).subscribe(contact => {
        });
      });
      const img = new SupplierImageCreateForm();
      img.supplierUuid = data.data.uuid;
      img.imageInfos = this.avatars;
      this.supplierService.createImages(img).subscribe(imgData => {

      });
    this.router.navigateByUrl('/warehouse/suppliers');
    }, error => {
      this.operationLock = false;
    });

  }

  public onBack(): void {
    this.router.navigateByUrl('/warehouse/suppliers');
  }
  public showNotification = (from: any, align: any) => {
    swal({
      title: 'Successful',
      buttonsStyling: false,
      confirmButtonClass: 'btn btn-success'
    }).catch(swal.noop);
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
      this.supplierForm.controls['imageUuid'].setValue(data.data.uuid);

      this.operationLock = false;
      this.ngxService.stop();

    });
  }
  public addOrderItem = () => {
    this.rows.push(new FormGroup({
      uuid: new FormControl(null),
      name: new FormControl(null, Validators.required),
      role: new FormControl({ value: null, disabled: false }, Validators.required),
      email: new FormControl(null, [Validators.required, Validators.email]),
      landLine: new FormControl({ value: null, disabled: false }, Validators.required),
      phoneNo: new FormControl({ value: null, disabled: false }, Validators.required)
    }, { updateOn: 'blur' }));
    this.dataSourceOrderItem.next(this.rows.controls);

  }
  public onDelete = (index: number, row: any) => {
    if (row.value.uuid === null) {
      this.rows.removeAt(index);
      this.dataSourceOrderItem.next(this.rows.controls);
    } else {
      this.supplierService.deleteContact(row.value.uuid).subscribe(data => {
        this.rows.removeAt(index);
        this.dataSourceOrderItem.next(this.rows.controls);
      });
    }
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

    const imgInfo = new SupplierImageCreateForm();
    imgInfo.supplierUuid = this.supplierForm.value.uuid;
    imgInfo.imageInfos = this.avatars;
    this.supplierService.createImages(imgInfo).subscribe(itm => {
      this.ngxService.stop();

    });

  }
  public getControl = (index, fieldName) => {
    const a = this.rows.at(index).get(fieldName) as FormControl;
    return this.rows.at(index).get(fieldName) as FormControl;
  }
}
