import { Component, OnInit, ViewChild } from '@angular/core';
import {Pageable} from '../../../../models/map/pagination';
import {FormArray, FormBuilder, FormGroup, ValidatorFn, Validators} from '@angular/forms';
import {MatTableDataSource, MatSort} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import swal from 'sweetalert2';
import { ToastrManager } from 'ng6-toastr-notifications';
import {SiteService} from '../../../../services/general/site.service';
import { SupplierService } from 'src/app/services/warehouse/supplier.service';
import { SupplierCreateUpdateForm } from 'src/app/models/suppliers/supplier-create-update-form';
import { SiteForm } from 'src/app/models/sites/site-form';

@Component({
  selector: 'app-sites-form',
  templateUrl: './sites-form.component.html',
  styleUrls: ['./sites-form.component.css'],
  providers: [SiteService]

})
export class SitesFormComponent implements OnInit {

  public description: string;
  public supplierDataSource = new MatTableDataSource<SupplierCreateUpdateForm>();
  public pageIndex: number;
  public pageSize: number;
  public lowValue: number;
  public highValue: number;
  public fileData: File = null;
  public page: Pageable;
  public siteForm: FormGroup;
  public operationLock: boolean;
  public validTextType: boolean;


  public siteDto: SiteForm;
  public dataSource = new MatTableDataSource<SiteForm>();

  constructor(private readonly formBuilder: FormBuilder,
              private readonly siteService: SiteService,
              private readonly supplierService: SupplierService,
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
      this.siteService.find(params.get('id')).subscribe(data => {
        this.siteForm.patchValue(data.data);
        this.siteForm.get('supplierUuid').setValue(data.data.supplierInfo.uuid);
      });
    });
    this.page = new Pageable();
    this.page.page = 0;
    this.siteService.list(this.page).subscribe(data => {
      this.dataSource.data = data.data.content;
    });

    this.supplierService.list(this.page).subscribe(data => {
      this.supplierDataSource.data = data.data.content;
    });
  }

  public createRegisterForm(): void {
    this.siteForm = this.formBuilder.group({
      uuid: [null],
      description: [null, Validators.required],
      supplierUuid: [null, Validators.required]
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
    this.operationLock = true;
    this.siteDto = new SiteForm();
    this.siteDto.uuid = this.siteForm.value.uuid;
    this.siteDto.description = this.siteForm.value.description;
    this.siteDto.supplierUuid = this.siteForm.value.supplierUuid;
    this.siteService.save(this.siteDto).subscribe(data => {
      this.operationLock = false;
      this.toast(data.type, data.text);
      this.router.navigateByUrl('/settings/sites');
    }, error => {
      this.operationLock = false;
    });
  }

  public onBack(): void {
    this.router.navigateByUrl('/settings/sites');
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
