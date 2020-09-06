import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { VehicleTypeService } from '../../../../services/vehicle/vehicle-type.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MatPaginator, MatSort, PageEvent } from '@angular/material';
import { SelectionModel } from '@angular/cdk/collections';
import { MatTableDataSource } from '@angular/material/table';
import swal from 'sweetalert2';
import { AttributesService } from '../../../../services/vehicle/attributes.service';
import { VehicleTypeAttributeDto } from 'src/app/models/map/VehicleTypeAttributeDto';
import { ToastrManager } from 'ng6-toastr-notifications';
import { Pageable } from 'src/app/models/map/pagination';
import { VehicleTypeCreateUpdateForm } from 'src/app/models/vehicle/vehicle-type-create-update-form';
declare const $: any;
@Component({
  selector: 'app-vehicle-type',
  templateUrl: './vehicle-type.component.html',
  styleUrls: ['./vehicle-type.component.css']
})
export class VehicleTypeComponent implements OnInit {
  public displayedColumns = ['select', 'name'];
  public dataSource = new MatTableDataSource<VehicleTypeAttributeDto>();
  public selection = new SelectionModel<VehicleTypeAttributeDto>(true, []);
  public operationLock: boolean;
  public page: Pageable;
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;
  public lowValue: number;
  public highValue: number;
  public vehicleTypeAttributeDto: VehicleTypeAttributeDto;
  public vehicleTypeAttributeDtos: any[];
  public vehicleTypeAttributeDtosTmp: any[];
  public frmGroup: FormGroup;
  public vehicleTypeDto: VehicleTypeCreateUpdateForm;
  public onLoading: boolean;
  public validTextType: boolean;
  @Input() public pageSizeOptions: number[] = [5, 10, 25, 50, 100];
  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;
  constructor(private readonly vehicleTypeService: VehicleTypeService,
    private readonly attrService: AttributesService,
    private readonly activatedRouter: ActivatedRoute,
    private readonly router: Router,
    private readonly formBuilder: FormBuilder,
    public toastr: ToastrManager) {
    this.pageIndex = 0;
    this.pageSize = 25;
    this.lowValue = 0;
    this.highValue = 25;
    this.validTextType = false;
    this.vehicleTypeAttributeDtos = [];
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
    this.frmGroup = new FormGroup({
      name: new FormControl(null, Validators.required),
      uuid: new FormControl()
    });
    this.activatedRouter.paramMap.subscribe(params => {
      if (params.has('id')) {
        this.vehicleTypeService.find(params.get('id')).subscribe(data => {
          this.frmGroup.patchValue(data.data);
          this.vehicleTypeAttributeDtos = data.data.vehicleTypeAttributes;
          data.data.vehicleTypeAttributes.forEach((item) => {
            this.dataSource.data.forEach(row => {
              if (item.uuid === row.uuid) {
                this.selection.select(row);
              }
            }); this.dataSource.data.forEach(row => {
              if (item.uuid === row.uuid) {
                this.selection.select(row);
              }
            });
          });
        });
      }
    });
    this.pageList(this.pageIndex, this.pageSize, true);
  }
  public createItem(): FormGroup {
    return this.formBuilder.group({
      attribute: new FormControl(null, Validators.required),
      uuid: new FormControl(),
    });
  }
  public ngOnSubmit(): void {
    if (this.frmGroup.invalid) {
      this.operationLock = false;
      this.validateAllFormFields(this.frmGroup);
      return;
    }
    this.vehicleTypeDto = new VehicleTypeCreateUpdateForm();
    this.vehicleTypeDto.vehicleTypeDesc = this.frmGroup.value.name;
    this.vehicleTypeDto.uuid = this.frmGroup.value.uuid;
    this.vehicleTypeAttributeDtosTmp = this.vehicleTypeAttributeDtos.filter((elem, index, self) => {
      return index === self.indexOf(elem);
    });
    this.vehicleTypeDto.vehicleTypeAttributeCreateUpdateForms = this.vehicleTypeAttributeDtosTmp;
    this.operationLock = true;
    this.vehicleTypeService.save(this.vehicleTypeDto).subscribe(data => {
      this.operationLock = false;
      this.toast(data.type, data.text);
      this.router.navigateByUrl('/settings/vehicle-types');
    }, error => {
      this.operationLock = false;
    });
  }
  public onBack(): void {

    this.router.navigateByUrl('/settings/vehicle-types');
  }
  public isAllSelected(): Boolean {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }
  public masterToggle(): void {
    this.isAllSelected() ?
      this.selection.clear() :
      this.dataSource.data.forEach(row => {
        this.selection.select(row);
        this.vehicleTypeAttributeDtos.push(row);
      });
  }
  public checkboxLabel(row?: VehicleTypeAttributeDto): string {
    if (!row) {
      return `${this.isAllSelected() ? 'select' : 'deselect'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.uuid + 1}`;
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
  public pageList(page: number, size: number, first: boolean): void {

    this.page = new Pageable();
    this.page.page = page;
    this.page.size = size;
    this.attrService.listWithValue(this.page).subscribe(data => {
      //    this.attrService.list(this.page).subscribe(data => {
      this.dataSource.data = data.data.content;
      this.selection.selected.forEach(it => {
        this.vehicleTypeAttributeDtos.push(it);

      });
      this.selection.clear();
      this.vehicleTypeAttributeDtos.forEach(item => {
        this.dataSource.data.forEach(row => {
          if (item.uuid === row.uuid) {
            this.selection.select(row);
            this.vehicleTypeAttributeDtos.push(row);


          }
        });
      });
      if (first) {
        this.dataSource.paginator = this.paginatorSubs;
        this.dataSource.sort = this.sortSubs;
      }
      this.onLoading = true;
      this.totalElement = data.data.totalElements;
    });
  }
  public serverData(event?: PageEvent): any {
    if (event.pageIndex === this.pageIndex + 1) {
      this.lowValue = this.lowValue + this.pageSize;
      this.highValue = this.highValue + this.pageSize;
    } else if (event.pageIndex === this.pageIndex - 1) {
      this.lowValue = this.lowValue - this.pageSize;
      this.highValue = this.highValue - this.pageSize;
    }
    this.pageIndex = event.pageIndex;
    this.pageList(event.pageIndex, event.pageSize, false);
    return event;
  }
  public onChange(event: any, item: any): void {
    if (event.checked) {
        this.vehicleTypeAttributeDtos.push(item);
        this.selection.select(item);
    } else {
        this.vehicleTypeAttributeDtosTmp = [];
        this.vehicleTypeAttributeDtos.forEach((row, index) => {
        if (item.uuid !== row.uuid) {
          this.vehicleTypeAttributeDtosTmp.push(row);
        }
      });
      this.vehicleTypeAttributeDtos = this.vehicleTypeAttributeDtosTmp;
    }
  }
  public textValidationType = (e) => {
    if (e) {
      this.validTextType = true;
    } else {
      this.validTextType = false;
    }
  }
  public validateAllFormFields = (formGroup: FormGroup) => {
    Object.keys(formGroup.controls).forEach(field => {
      const control = formGroup.get(field);
      if (control instanceof FormControl) {
        control.markAsTouched({ onlySelf: true });
      } else if (control instanceof FormGroup) {
        this.validateAllFormFields(control);
      }
    });
  }

}
