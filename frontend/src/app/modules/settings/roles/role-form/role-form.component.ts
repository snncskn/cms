import { Component, OnInit } from '@angular/core';
import {Pageable} from '../../../../models/map/pagination';
import {FormArray, FormBuilder, FormGroup, ValidatorFn, Validators} from '@angular/forms';
import {MatTableDataSource} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import swal from 'sweetalert2';
import { ToastrManager } from 'ng6-toastr-notifications';
import {RoleCreateUpdateForm} from '../../../../models/roles/role-create-update-form';
import {RoleService} from '../../../../services/role/role.service';

@Component({
  selector: 'app-role-form',
  templateUrl: './role-form.component.html',
  styleUrls: ['./role-form.component.scss'],
  providers: [RoleService]

})
export class RoleFormComponent implements OnInit {

  public name: string;
  public uuid: string;
  public pageIndex: number;
  public pageSize: number;
  public lowValue: number;
  public highValue: number;
  public fileData: File = null;
  public page: Pageable;
  public roleForm: FormGroup;
  public operationLock: boolean;
  public validTextType: boolean;

  public roleDto: RoleCreateUpdateForm;
  public dataSource = new MatTableDataSource<RoleCreateUpdateForm>();

  constructor(private readonly formBuilder: FormBuilder,
              private readonly roleService: RoleService,
              private readonly activatedRouter: ActivatedRoute,
              private readonly router: Router,
              public toastr: ToastrManager) {
    this.pageIndex = 0;
    this.pageSize = 5;
    this.lowValue = 0;
    this.highValue = 5;
    this.createRegisterForm();
    this.validTextType = false;
  }

  public isFieldValid = (form: FormGroup, field: string) => {
    return !form.get(field).valid && form.get(field).touched;
  }

  public displayFieldCss = (form: FormGroup, field: string) => {
    return {
      'has-error': this.isFieldValid(form, field),
      'has-feedback': this.isFieldValid(form, field)
    };
  }

  public ngOnInit(): void {

    this.activatedRouter.paramMap.subscribe(params => {
      this.roleService.find(params.get('id')).subscribe(data => {
        this.roleForm.patchValue(data.data);
      });
    });
    this.page = new Pageable();
    this.page.page = 0;
    this.roleService.list(this.page).subscribe(data => {
      this.dataSource.data = data.data.content;
    });
  }

  public createRegisterForm(): void {
    this.roleForm = this.formBuilder.group({
      uuid: [null],
      name: [null, Validators.required]
    });
  }
  public textValidationType = (e) => {
    if (e) {
      this.validTextType = true;
    } else {
      this.validTextType = false;
    }
  }


  public onSubmit(): void {

      this.roleDto = new RoleCreateUpdateForm();
      this.roleDto.name = this.roleForm.value.name;
      if (this.roleForm.value.uuid === null) {
          this.operationLock = true;
          this.roleService.save(this.roleDto).subscribe(data => {
              this.operationLock = false;
              this.toast(data.type, data.text);
              this.router.navigateByUrl('/settings/roles');
          }, error => {
              this.operationLock = false;
          });
      } else  {
          this.roleDto.uuid = this.roleForm.value.uuid;
          this.roleService.update(this.roleForm.value.uuid, this.roleDto).subscribe(data => {
              this.operationLock = false;
              this.toast(data.type, data.text);
              this.router.navigateByUrl('/settings/roles');
          }, error => {
              this.operationLock = false;
          });
      }
  }
  public onBack(): void {
    this.router.navigateByUrl('/settings/roles');
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
}
