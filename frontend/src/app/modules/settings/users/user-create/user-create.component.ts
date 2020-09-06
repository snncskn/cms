import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { Pageable } from '../../../../models/map/pagination';
import { Image } from '../../../../models/general/image';
import { ImageService } from '../../../../services/general/image.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrManager } from 'ng6-toastr-notifications';
import { UserService } from '../../../../services/general/user.service';
import { MatTableDataSource } from '@angular/material';
import { UserInfo } from '../../../../models/users/user-info';
import { DatePipe } from '@angular/common';
import { UserForm } from 'src/app/models/users/userForm';
import { SiteService } from 'src/app/services/general/site.service';
import { SiteForm } from 'src/app/models/sites/site-form';
import { PropertyForm } from 'src/app/models/property/property-form';
import { PropertyService } from 'src/app/services/general/property.service';
import { RoleService } from 'src/app/services/general/role.services';
import { UserRole } from 'src/app/models/users/user-role';
import { RoleForm } from 'src/app/models/roles/role-form';


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
  selector: 'app-user-create',
  templateUrl: './user-create.component.html',
  providers: [UserService]
})

export class UserCreateComponent implements OnInit {
  public items: FormArray;
  public userForm: FormGroup;
  public fileData: File = null;
  public page: Pageable;
  public operationLock: boolean;
  public image: Image;
  public pageIndex: number;
  public pageSize: number;
  public lowValue: number;
  public highValue: number;
  public user: UserInfo;
  public siteInfo: SiteForm;
  public roleInfo: RoleForm;
  public dataSource = new MatTableDataSource<UserForm>();
  public dataSourceSite = new MatTableDataSource<SiteForm>();
  public dataSourceRole = new MatTableDataSource<UserRole>();
  public imageList: Image[];
  public siteInfos = new FormControl();
  public roleInfos = new FormControl();
  public siteInfosEdit: string[];
  public roleInfosEdit: string[];
  @ViewChild('fileImage') public fileImage: ElementRef;
  public editMode: boolean;
  public propertys: PropertyForm[];



  constructor(private readonly formBuilder: FormBuilder,
    private readonly userService: UserService,
    private readonly siteService: SiteService,
    private readonly imageService: ImageService,
    private readonly activatedRouter: ActivatedRoute,
    private readonly roleService: RoleService,
    private readonly propertyService: PropertyService,
    private readonly router: Router,
    private readonly datePipe: DatePipe,
    public toastr: ToastrManager) {

    this.imageList = [];
    this.pageIndex = 0;
    this.pageSize = 25;
    this.lowValue = 0;
    this.highValue = 25;
    this.createRegisterForm();
    this.image = new Image();
    this.image.downloadUrl = './assets/img/placeholder.jpg';
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = 25;
    this.propertyService.findByGroupName(this.page, 'USER_GROUP').subscribe(data => {
      this.propertys = data.data.content;
    });
  }
  public createRegisterForm(): void {
    this.userForm = this.formBuilder.group({
      uuid: [null],
      site: [null],
      firstName: [null, Validators.required],
      position: [null, Validators.required],
      lastName: [null, Validators.required],
      email: [null, [Validators.required, Validators.email]],
      contactNumber: [null, Validators.required],
       password: [null, Validators.required],
       verifyPassword: [null, Validators.required],
      expireDate: [null, Validators.required],
      imageUuid: [null],
      staffNumber: [null]
    });
  }

  public MustMatch = (controlName: string, matchingControlName: string) => (formGroup: FormGroup) => {
    const control = formGroup.controls[controlName];
    const matchingControl = formGroup.controls[matchingControlName];

    if (matchingControl.errors && !matchingControl.errors.mustMatch) {
      return;
    }
    if (control.value !== matchingControl.value) {
      matchingControl.setErrors({ mustMatch: true });
    } else {
      matchingControl.setErrors(null);
    }
  }

  public ngOnInit(): void {
    this.activatedRouter.paramMap.subscribe(params => {
      if (params.has('id')) {
        this.userService.find(params.get('id')).subscribe(data => {
          this.userForm.patchValue(data.data);
          this.userForm.controls['password'].clearValidators();
          this.userForm.controls['verifyPassword'].clearValidators();
          this.userForm.controls['verifyPassword'].updateValueAndValidity();
          this.userForm.controls['password'].updateValueAndValidity();
          this.editMode = true;
          if (data.data.imageInfo !== null
            || data.data.imageInfo !== undefined) {
            this.image = data.data.imageInfo;

          }
          this.siteInfosEdit = [];
          data.data.siteInfos.forEach(item => {
            this.siteInfosEdit.push(item.uuid);
          });
          this.siteInfos.setValue(this.siteInfosEdit);

          this.roleInfosEdit = [];
          data.data.roleInfos.forEach(item => {
            this.roleInfosEdit.push(item.uuid);
          });
          this.roleInfos.setValue(this.roleInfosEdit);
        });
      }
    });
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = 25;
    this.userService.list(this.page).subscribe(data => {
      this.dataSource.data = data.data.content;
    });
    this.siteService.list(this.page).subscribe(data => {
      this.dataSourceSite.data = data.data.content;
    });
    this.roleService.list(this.page).subscribe(rol => {
      this.dataSourceRole.data = rol.data.content;
    });
  }
  public onSubmit(): void {
    if (!this.userForm.valid) {
      this.toastr.errorToastr('ERROR', 'Invalid data');
      return;
    }
    this.operationLock = true;
    this.user = new UserInfo();
    this.user.uuid = this.userForm.value.uuid;
    this.user.firstName = this.userForm.value.firstName;
    this.user.lastName = this.userForm.value.lastName;
    this.user.position = this.userForm.value.position;
    this.user.staffNumber = this.userForm.value.staffNumber;
    this.user.contactNumber = this.userForm.value.contactNumber;
    if (!this.editMode) {
      this.user.password = this.userForm.value.password;
      this.user.verifyPassword = this.userForm.value.verifyPassword;
    }
    this.user.expireDate = this.datePipe.transform(this.userForm.value.expireDate, 'yyyy-MM-dd HH:mm');
    this.user.imageUuid = this.userForm.value.imageUuid;
    this.user.email = this.userForm.value.email;
    this.user.imageInfos = this.imageList;
    this.user.imageInfo = this.image;
    this.user.siteInfos = [];
    this.user.roleInfos = [];
    this.siteInfos.value.forEach(item => {
      this.siteInfo = new SiteForm();
      this.siteInfo.uuid = item;
      this.user.siteInfos.push(this.siteInfo);
    });

    this.roleInfos.value.forEach(item => {
      this.roleInfo = new RoleForm();
      this.roleInfo.uuid = item;
      this.user.roleInfos.push(this.roleInfo);
    });

    if (this.userForm.value.uuid !== null) {
      this.userService.update(this.user).subscribe(data => {
        this.operationLock = false;
        this.toast(data.type, data.text);
        this.router.navigateByUrl('/settings/users');
      }, error => {
        this.operationLock = false;
        this.toast('ERROR', error.text);
      });
    } else {
      this.userService.save(this.user).subscribe(data => {
        this.operationLock = false;
        this.toast(data.type, data.text);
        this.router.navigateByUrl('/settings/users');
      }, error => {
        this.operationLock = false;
        this.toast('ERROR', error.text);
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
      this.image.downloadUrl = './assets/img/placeholder.jpg';
      this.fileImage.nativeElement.value = '';
      this.operationLock = false;

      return;

    }
    if (this.fileData.size > 1048576) {
      this.toastr.warningToastr('File size max: 1MB ', 'File Size');
      this.image = new Image();
      this.image.downloadUrl = './assets/img/placeholder.jpg';
      this.fileImage.nativeElement.value = '';
      this.operationLock = false;

      return;
    }

    this.imageService.upload(this.fileData).subscribe(data => {
      this.userForm.controls['imageUuid'].setValue(data.data.uuid);
      this.operationLock = false;
      this.image = new Image();
      this.image.uuid = data.data.uuid;
      this.image.downloadUrl = data.data.downloadUrl;
      this.imageList.push(this.image);
    });

  }
  public onBack(): void {
    this.router.navigateByUrl('/settings/users');
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
  public siteDesc(): string {
    let label = '';
    this.dataSourceSite.data.forEach((item, index) => {
      if (!this.siteInfos.value) {
        return '';
      }
      this.siteInfos.value.forEach(element => {
        if (item.uuid === element) {
          label = label + '' + item.description + ' ,';
        }
      });

    });
    return label;
  }
  public roleDesc(): string {
    let label = '';
    this.dataSourceRole.data.forEach((item, index) => {
      if (!this.roleInfos.value) {
        return '';
      }
      this.roleInfos.value.forEach(element => {
        if (item.uuid === element) {
          label = label + '' + item.name + ' ,';
        }
      });

    });
    return label;
  }

}
