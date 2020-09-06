import {
  Component, OnInit, OnChanges, AfterViewInit, SimpleChanges,
  ViewChild, AfterContentInit, ElementRef
} from '@angular/core';
import { FormControl, FormGroupDirective, NgForm, Validators, FormGroup } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { FormBuilder } from '@angular/forms';
import { AttributesService } from '../../../../services/vehicle/attributes.service';
import { MatPaginator, MatSort, MatTableDataSource } from '@angular/material';
import { ActivatedRoute, Router } from '@angular/router';
import { VehicleTypeService } from '../../../../services/vehicle/vehicle-type.service';
import { VehicleAttributeService } from '../../../../services/vehicle/vehicle-attribute.service';
import { VehicleTypeAttributeValueService } from '../../../../services/vehicle/vehicle-type-attribute-value.service';
import swal from 'sweetalert2';
import { VehicleAttributeCreateUpdateForm } from 'src/app/models/map/vehicle-attribute-create-update-form';
import { VehicleAttributeValueCreateUpdateForm } from 'src/app/models/map/vehicle-attribute-value-create-update-form';
import { Pageable } from 'src/app/models/map/pagination';
import { ToastrManager } from 'ng6-toastr-notifications';
import { VehicleType } from 'src/app/models/map/vehicle-type';


declare const $: any;
interface FileReaderEventTarget extends EventTarget {
  result: string;
}

interface FileReaderEvent extends Event {
  target: EventTarget;
  getMessage(): string;
}

export class MyErrorStateMatcher implements ErrorStateMatcher {
  public isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}



@Component({
  selector: 'app-attributes-form',
  templateUrl: './attributes-form.component.html',
  styleUrls: ['./attributes-form.component.css'],
  providers: [AttributesService, VehicleTypeService]
})
export class AttributesFormComponent implements OnInit, OnChanges, AfterViewInit, AfterContentInit {


  public frmGroup: FormGroup;
  public matcher = new MyErrorStateMatcher();
  public displayedColumns = ['vehicleAttributeValue', 'btn'];
  public dataSource = new MatTableDataSource<VehicleAttributeValueCreateUpdateForm>();
  public vehicleAttributeForm: VehicleAttributeCreateUpdateForm;
  public vehicleAttributeValueForm: VehicleAttributeValueCreateUpdateForm;
  public vehicleTypes: VehicleType[];
  public selectedUuid: string;

  public page: Pageable;
  public totalElement: number;
  public pageSize: number;
  public operationLock: boolean;
  public tstBoolean: boolean;



  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;
  @ViewChild('attribute') public attribute: ElementRef;

  public onLoading: boolean;


  constructor(private readonly attributeservice: AttributesService,
    private readonly vehicleTypeService: VehicleTypeService,
    private readonly vehicleAttributeValueService: VehicleTypeAttributeValueService,
    private readonly vehicleAttributeService: VehicleAttributeService,
    private readonly activatedRouter: ActivatedRoute,
    private readonly formBuilder: FormBuilder,
    private readonly router: Router,
    public toastr: ToastrManager) {
      this.pageSize = 5;
      this.tstBoolean = false;
  }

  public ngAfterContentInit(): void {
    this.attribute.nativeElement.focus();

  }

  public isFieldValid(form: FormGroup, field: string): Boolean {
    return !form.get(field).valid && form.get(field).touched;
  }

  public displayFieldCss(form: FormGroup, field: string): any {
    return {
      'has-error': this.isFieldValid(form, field),
      'has-feedback': this.isFieldValid(form, field)
    };
  }


  public ngOnInit(): void {
    const tht = this;
    this.operationLock = false;
    this.vehicleTypeService.list2().subscribe(data => {
      this.vehicleTypes = data.data.content;
    });

    this.frmGroup = this.formBuilder.group({
      vehicleTypeUUID: [null, Validators.requiredTrue],
      name: [null, Validators.required],
      attributeValueUuid: [null, Validators.required],
      value: [null, Validators.required],
      search: [null, Validators.required],
      uuid: [null, Validators.required],
    });

    this.find();
    const $validator = $('.card-wizard form').validate({
      rules: {
        name: {
          required: true,
          minlength: 3
        },
        vehicleType: {
          required: true
        },
        value: {
          required: true
        }
      },

      highlight: function (element: any): void {
        $(element).closest('.form-group').removeClass('has-success').addClass('has-danger');
      },
      success: function (element: any): void {
        $(element).closest('.form-group').removeClass('has-danger').addClass('has-success');
      },
      errorPlacement: function (error: any, element: any): void {
        $(element).append(error);
      }
    });

    $('.card-wizard').bootstrapWizard({
      'tabClass': 'nav nav-pills',
      'nextSelector': '.btn-next',
      'previousSelector': '.btn-previous',

      onNext: function (tab: any, navigation: any, index: any): Boolean {
        this.operationLock = false;
        const $valid = $('.card-wizard form').valid();
        if (!$valid) {
          $validator.focusInvalid();
          return false;
        } else {
          if (tht.frmGroup.value.name === null || tht.frmGroup.value.name.trim() === '' ) {
            return false;
          }
          tht.vehicleAttributeForm = new VehicleAttributeCreateUpdateForm();
          tht.vehicleAttributeForm.vehicleAttribute = tht.frmGroup.value.name;
          tht.vehicleAttributeForm.uuid = tht.frmGroup.value.uuid;

          tht.attributeservice.save(tht.vehicleAttributeForm).subscribe(data => {
            tht.frmGroup.get('uuid').setValue(data.data.uuid);
            tht.operationLock = true;
            tht.toast(data.type, data.text);
          }, error => {
            tht.operationLock = true;
          });
        }
      },

      onInit: function (tab: any, navigation: any, index: any): void {

        // check number of tabs and fill the entire row
        let $total = navigation.find('li').length;
        const $wizard = navigation.closest('.card-wizard');

        const $first_li = navigation.find('li:first-child a').html();
        const $moving_div = $('<div class="moving-tab">' + $first_li + '</div>');
        $('.card-wizard .wizard-navigation').append($moving_div);

        $total = $wizard.find('.nav li').length;
        let $li_width = 100 / $total;

        const total_steps = $wizard.find('.nav li').length;
        let move_distance = $wizard.width() / total_steps;
        let index_temp = index;
        let vertical_level = 0;

        const mobile_device = $(document).width() < 600 && $total > 3;

        if (mobile_device) {
          move_distance = $wizard.width() / 2;
          index_temp = index % 2;
          $li_width = 50;
        }

        $wizard.find('.nav li').css('width', $li_width + '%');

        const step_width = move_distance;
        move_distance = move_distance * index_temp;

        const $current = index + 1;

        if ($current === 1 || (mobile_device === true && (index % 2 === 0))) {
          move_distance -= 8;
        } else if ($current === total_steps || (mobile_device === true && (index % 2 === 1))) {
          move_distance += 8;
        }
        if (mobile_device) {
          const x: any = index / 2;
          vertical_level = parseInt(x, 10);
          vertical_level = vertical_level * 38;
        }

        $wizard.find('.moving-tab').css('width', step_width);
        $('.moving-tab').css({
          'transform': 'translate3d(' + move_distance + 'px, ' + vertical_level + 'px, 0)',
          'transition': 'all 0.5s cubic-bezier(0.29, 1.42, 0.79, 1)'

        });
        $('.moving-tab').css('transition', 'transform 0s');
      },

      onTabClick: function (tab: any, navigation: any, index: any): Boolean {

        const $valid = $('.card-wizard form').valid();
        if (tht.frmGroup.value.name === null || tht.frmGroup.value.name.trim() === '' ) {
          return false;
        }
        if (!$valid) {
          return false;
        } else {
          return true;
        }
      },

      onTabShow: function (tab: any, navigation: any, index: any): void {
        let $total = navigation.find('li').length;
        let $current = index + 1;

        const $wizard = navigation.closest('.card-wizard');

        // If it's the last tab then hide the last button and show the finish instead
        if ($current >= $total) {
          $($wizard).find('.btn-next').hide();
          $($wizard).find('.btn-finish').show();
        } else {
          $($wizard).find('.btn-next').show();
          $($wizard).find('.btn-finish').hide();
        }

        const button_text = navigation.find('li:nth-child(' + $current + ') a').html();

        setTimeout(function (): void {
          $('.moving-tab').text(button_text);
        }, 150);

        const checkbox = $('.footer-checkbox');

        if (index !== 0) {
          $(checkbox).css({
            'opacity': '0',
            'visibility': 'hidden',
            'position': 'absolute'
          });
        } else {
          $(checkbox).css({
            'opacity': '1',
            'visibility': 'visible'
          });
        }
        $total = $wizard.find('.nav li').length;
        let $li_width = 100 / $total;

        const total_steps = $wizard.find('.nav li').length;
        let move_distance = $wizard.width() / total_steps;
        let index_temp = index;
        let vertical_level = 0;

        const mobile_device = $(document).width() < 600 && $total > 3;

        if (mobile_device) {
          move_distance = $wizard.width() / 2;
          index_temp = index % 2;
          $li_width = 50;
        }

        $wizard.find('.nav li').css('width', $li_width + '%');

        const step_width = move_distance;
        move_distance = move_distance * index_temp;

        $current = index + 1;

        if ($current === 1 || (mobile_device === true && (index % 2 === 0))) {
          move_distance -= 8;
        } else if ($current === total_steps || (mobile_device === true && (index % 2 === 1))) {
          move_distance += 8;
        }

        if (mobile_device) {
          const x: any = index / 2;
          vertical_level = parseInt(x, 10);
          vertical_level = vertical_level * 38;
        }

        $wizard.find('.moving-tab').css('width', step_width);
        $('.moving-tab').css({
          'transform': 'translate3d(' + move_distance + 'px, ' + vertical_level + 'px, 0)',
          'transition': 'all 0.5s cubic-bezier(0.29, 1.42, 0.79, 1)'

        });
      }
    });
    $('#wizard-picture').change(function (): void {
      const input = $(this);

      if (input[0].files && input[0].files[0]) {
        const reader = new FileReader();

        reader.onload = function (e: any): void {
          $('#wizardPicturePreview').attr('src', e.target.result).fadeIn('slow');
        };
        reader.readAsDataURL(input[0].files[0]);
      }
    });

    $('[data-toggle="wizard-radio"]').click(function (): void {
      const wizard = $(this).closest('.card-wizard');
      wizard.find('[data-toggle="wizard-radio"]').removeClass('active');
      $(this).addClass('active');
      $(wizard).find('[type="radio"]').removeAttr('checked');
      $(this).find('[type="radio"]').attr('checked', 'true');
    });

    $('[data-toggle="wizard-checkbox"]').click(function (): void {
      if ($(this).hasClass('active')) {
        $(this).removeClass('active');
        $(this).find('[type="checkbox"]').removeAttr('checked');
      } else {
        $(this).addClass('active');
        $(this).find('[type="checkbox"]').attr('checked', 'true');
      }
    });

    $('.set-full-height').css('height', 'auto');

  }

  public ngOnChanges(changes: SimpleChanges): void {
    const input = $(this);

    if (input[0].files && input[0].files[0]) {
      const reader: any = new FileReader();

      reader.onload = function (e: any): void {
        $('#wizardPicturePreview').attr('src', e.target.result).fadeIn('slow');
      };
      reader.readAsDataURL(input[0].files[0]);
    }
  }
  public ngAfterViewInit(): void {

    $(window).resize(() => {
      $('.card-wizard').each(function (): void {

        const $wizard = $(this);
        const index = $wizard.bootstrapWizard('currentIndex');
        const $total = $wizard.find('.nav li').length;
        let $li_width = 100 / $total;

        const total_steps = $wizard.find('.nav li').length;
        let move_distance = $wizard.width() / total_steps;
        let index_temp = index;
        let vertical_level = 0;

        const mobile_device = $(document).width() < 600 && $total > 3;

        if (mobile_device) {
          move_distance = $wizard.width() / 2;
          index_temp = index % 2;
          $li_width = 50;
        }

        $wizard.find('.nav li').css('width', $li_width + '%');

        const step_width = move_distance;
        move_distance = move_distance * index_temp;

        const $current = index + 1;

        if ($current === 1 || (mobile_device === true && (index % 2 === 0))) {
          move_distance -= 8;
        } else if ($current === total_steps || (mobile_device === true && (index % 2 === 1))) {
          move_distance += 8;
        }

        if (mobile_device) {
          const x: any = index / 2;
          vertical_level = parseInt(x, 10);
          vertical_level = vertical_level * 38;
        }

        $wizard.find('.moving-tab').css('width', step_width);
        $('.moving-tab').css({
          'transform': 'translate3d(' + move_distance + 'px, ' + vertical_level + 'px, 0)',
          'transition': 'all 0.5s cubic-bezier(0.29, 1.42, 0.79, 1)'
        });

        $('.moving-tab').css({
          'transition': 'transform 0s'
        });
      });
    });
  }
  public createItem(): FormGroup {
    return this.formBuilder.group({
      uuid: new FormControl(),
      name: new FormControl(),
      vehicleType: new FormGroup({
        uuid: new FormControl(),
      })
    });
  }
  public onAddValue(): void {
    this.operationLock = false;
    const _controller = this;
    if (this.frmGroup.value.value === null
      || this.frmGroup.value.value === '') {
      this.operationLock = true;
      return;
    }
    this.vehicleAttributeValueForm = new VehicleAttributeValueCreateUpdateForm();
    this.vehicleAttributeValueForm.desc = this.frmGroup.value.value.toLocaleUpperCase();
    this.vehicleAttributeValueForm.attributeValueUuid = this.frmGroup.value.attributeValueUuid;
    this.vehicleAttributeValueForm.vehicleAttributeUuid = this.frmGroup.value.uuid;
    this.vehicleAttributeValueService.save(this.vehicleAttributeValueForm).subscribe(data => {
      this.find();
      _controller.frmGroup.get('value').setValue('');
      _controller.frmGroup.get('attributeValueUuid').setValue('');
      this.operationLock = true;
    }, error => {
    this.operationLock = true;
      this.operationLock = true;
    });
    this.selectedUuid = '';
  }
  public onFinish(): void {
    this.router.navigateByUrl('/settings/vehicle-attributes');
  }
  public onDelete = (uuid) => {
    const _controller = this;
    swal({
      title: 'Are you sure you want to delete this Attribute ?',
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
        _controller.vehicleAttributeValueService.delete(uuid).subscribe(data => {
          _controller.toast(data.type, data.text);
          _controller.find();

        });

      } else {
      }
    });
  }
  public showNotification = (from: any, align: any) => {

    swal({
      title: 'Deleted Successfully',
      buttonsStyling: false,
      confirmButtonClass: 'btn btn-success'
    }).catch(swal.noop);

  }
  public find(): void {
    let isT: string;
    if (this.frmGroup.value.uuid != null) {
      isT = this.frmGroup.value.uuid;
    }
    this.activatedRouter.paramMap.subscribe(params => {
      if (params.keys.length > 0) {
        isT = params.get('id');
      }
    });
    if (isT !== '' && isT !== undefined) {
      this.attributeservice.find(isT).subscribe(data => {
        this.frmGroup.patchValue(data.data);

        this.dataSource.data = data.data.vehicleAttributeValues;
        this.dataSource.paginator = this.paginatorSubs;
        this.dataSource.sort = this.sortSubs;
        this.onLoading = true;

      });
    }
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
  public onKeydown(event: any): void {
    if (event.ctrlKey && event.code === 'ControlLeft') {
      this.router.navigateByUrl('/settings/vehicle-attributes');
    }
    if (event.key === 'Enter') {
      if (this.frmGroup.value.value != null) {
        if (this.frmGroup.value.value.trim().length > 0) {
          this.onAddValue();
        }
      }
    }
  }
  public onKeydownAttr = event => {
    if (event.key === 'Enter') {
      this.tstBoolean = true;
      this.vehicleAttributeForm = new VehicleAttributeCreateUpdateForm();
      this.vehicleAttributeForm.vehicleAttribute = this.frmGroup.value.name.toLocaleUpperCase();
      this.vehicleAttributeForm.uuid = this.frmGroup.value.uuid;

      this.attributeservice.save(this.vehicleAttributeForm).subscribe(data => {
        this.frmGroup.get('uuid').setValue(data.data.uuid);
        this.operationLock = true;
        this.toast(data.type, data.text);
        document.getElementById('tabDef').click();
        document.getElementById('val').focus();
        }, error => {
        this.operationLock = true;
      });

      this.operationLock = false;
    }
  }

  public onEdit(row: VehicleAttributeValueCreateUpdateForm): void {
    this.frmGroup.controls['attributeValueUuid'].setValue(row.uuid);
    this.selectedUuid = row.uuid;
    this.frmGroup.controls['value'].setValue(row.vehicleAttributeValue);

  }
  public onBack(): void {
    this.router.navigateByUrl('/settings/vehicle-attributes');
  }

  public classRow = (row: any) => {
    if (row.uuid === this.selectedUuid) {
      return 'table-purchase';
    }
  }

  public onClickDef(): void {
    this.vehicleAttributeForm = new VehicleAttributeCreateUpdateForm();
    this.vehicleAttributeForm.vehicleAttribute = this.frmGroup.value.name.toLocaleUpperCase();
    this.vehicleAttributeForm.uuid = this.frmGroup.value.uuid;
    this.attributeservice.save(this.vehicleAttributeForm).subscribe(data => {
      this.frmGroup.get('uuid').setValue(data.data.uuid);
      this.operationLock = true;
      if (!this.tstBoolean) {
        this.toast(data.type, data.text);
      }
      document.getElementById('val').focus();
    }, error => {
      this.operationLock = true;
    });
  }
}
