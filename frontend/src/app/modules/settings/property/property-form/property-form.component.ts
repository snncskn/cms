import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, ValidatorFn, Validators} from '@angular/forms';
import {Pageable} from '../../../../models/map/pagination';
import {MatDialog, MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import {PropertyService} from '../../../../services/general/property.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrManager} from 'ng6-toastr-notifications';
import {PropertyForm} from '../../../../models/property/property-form';
import {Property} from '../../../../models/property/property';

@Component({
  selector: 'app-property-form',
  templateUrl: './property-form.component.html',
  styleUrls: ['./property-form.component.scss']
})
export class PropertyFormComponent implements OnInit, AfterViewInit {

  public attributesPanel: boolean;
  public items: FormArray;
  public propertyForm: FormGroup;
  public page: Pageable;
  public operationLock: boolean;
  public currentIndex: number;
  public carouselShow = false;
  public propery: Property;
  public groupName: string;
  public keyLabel: string;
  public keyValue: string;
  public dataSource = new MatTableDataSource<PropertyForm>();
  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;

  constructor(private readonly formBuilder: FormBuilder,
              private readonly propertyService: PropertyService,
              private readonly activatedRouter: ActivatedRoute,
              private readonly router: Router,
              private readonly dialog: MatDialog,
              public toastr: ToastrManager
  ) {
    this.createRegisterForm();
    this.currentIndex = 0;
    this.propery = new Property();

    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = 99;
  }


  public createRegisterForm(): void {
    this.propertyForm = this.formBuilder.group({
      uuid: [null],
      groupName: [null, Validators.required],
      keyLabel: [null, Validators.required],
      keyValue: [null, Validators.required],
    });
  }

  public ngOnInit(): any {
    this.activatedRouter.paramMap.subscribe(params => {
      this.propertyService.find(params.get('id')).subscribe(data => {
        this.propertyForm.patchValue(data.data);
      });
    });
    this.operationLock = false;
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = 100;
    this.propertyService.list(this.page).subscribe(data => {
      this.dataSource.data = data.data.content;
    });

  }

  public ngAfterViewInit(): void {
    this.dataSource.sort = this.sortSubs;
    this.dataSource.paginator = this.paginatorSubs;

  }

  public minSelectedCheckboxes(): ValidatorFn {
    const validator: ValidatorFn = (formArray: FormArray) => {

      const selectedCount = formArray.controls
          .map(control => control.value)
          .reduce((prev, next) => next ? prev + next : prev, 0);

      return selectedCount >= 1 ? null : {notSelected: 0};
    };

    return validator;
  }



  public onSubmit(): void {
    if (!this.propertyForm.valid) {
      this.toastr.errorToastr('Please check required field', 'Valitadion error');
    } else {
      this.propery.groupName = this.propertyForm.value.groupName;
      this.propery.keyLabel = this.propertyForm.value.keyLabel;
      this.propery.keyValue = this.propertyForm.value.keyValue;
      if (this.propertyForm.value.uuid === null) {
        this.propertyService.save(this.propery).subscribe(data => {
          this.toast(data.type, data.text);
          this.propertyForm.patchValue(data);
          this.router.navigateByUrl('settings/propertys');
        }, error => {
          this.operationLock = false;
        });
      } else {
        this.propertyService.update(this.propertyForm.value.uuid, this.propery).subscribe(data => {
          this.propertyForm.patchValue(data);
          this.router.navigateByUrl('settings/propertys');
        }, error => {
          this.operationLock = false;
        });
      }
    }
  }
  public onBack(): void {
    this.router.navigateByUrl('settings/propertys');
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

  public buttonActiveChecker(): void {
    const state = document.getElementById('button');
    if (this.operationLock === false) {
      state.setAttribute('style', 'background-color:purple');
    }
  }

  public onCarouselShow(): void {
    this.carouselShow = true;
  }

}
