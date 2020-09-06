import { Component, OnInit, AfterViewInit, ViewChild, ɵConsole, ElementRef } from '@angular/core';
import { VehicleService } from 'src/app/services/vehicle/vehicle.service';
import { Router } from '@angular/router';
import { MatTableDataSource, MatSort, MatPaginator, PageEvent } from '@angular/material';
import swal from 'sweetalert2';
import { Pageable } from 'src/app/models/map/pagination';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrManager } from 'ng6-toastr-notifications';
import { Vehicle } from 'src/app/models/vehicle/vehicle-info';
import { VehicleFilter, VehicleTypeFilter } from 'src/app/models/vehicle/vehicle-filter';
import { UserService } from 'src/app/services/general/user.service';
import { User } from 'src/app/models/general/user';
import { UserForm } from 'src/app/models/users/userForm';
import { PropertyService } from 'src/app/services/general/property.service';
import { PropertyForm } from 'src/app/models/property/property-form';
import { Image } from 'src/app/models/general/image';
import { BreakDownService } from 'src/app/services/workshop/break-down.service';
import { BreakDown } from 'src/app/models/workshop/break-down/break-down';
import { DatePipe } from '@angular/common';
import { ImageService } from 'src/app/services/general/image.service';
import { BreakDownImage } from 'src/app/models/workshop/break-down/break-down-image';
import { debounceTime, switchMap, finalize, tap, withLatestFrom, startWith, map } from 'rxjs/operators';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { ExcelService } from 'src/app/services/general/excel.service';
import { WebcamImage, WebcamInitError, WebcamUtil } from 'ngx-webcam';
import { Observable, Subject } from 'rxjs';
import { trigger, state, style, transition, animate } from '@angular/animations';

declare const $: any;
@Component({
    selector: 'app-vehicle-list',
    templateUrl: './vehicle-list.component.html',
    styleUrls: ['./vehicle-list.component.css', './vehicle-list.component.scss'],
    providers: [VehicleService],
    animations: [ trigger('fadeInOut', [
        state('open', style({
          opacity: 10
        })),
        transition('void <=> *', animate(1000)),
      ])]
})
export class VehicleListComponent implements OnInit, AfterViewInit {
    public displayedColumns = ['fleetNo', 'vehicleTypeDesc',
        'currentMachineHours', 'serviceIntervalHours',
        'lastServiceDate', 'lastServiceHours', 'remainingHours', 'btn', 'excel'];
    public displayedColumnsImage = ['name', 'btn'];
    public dataSource = new MatTableDataSource<Vehicle>();
    public dataSourceUser = new MatTableDataSource<UserForm>();
    public et_users = new MatTableDataSource<UserForm>();




    public dataSourceImage = new MatTableDataSource<Image>();
    public page: Pageable;
    public totalElement: number;
    public pageIndex: number;
    public pageSize: number;
    public lowValue: number;
    public highValue: number;
    public vehicleForm: FormGroup;
    public breakDown: FormGroup;
    public onLoading: boolean;
    public vehicleFilter: VehicleFilter;
    public propertys: PropertyForm[];
    public operationLock: boolean;
    public fileData: File = null;
    public image: Image;
    public avatars = [];
    public imageList = [];
    public currentIndex: number;
    public speed = 5000;
    public infinite = true;
    public direction = 'right';
    public directionToggle = true;
    public cameraDialog = false;

    public autoplay = true;
    public carouselShow: boolean;
    public type: string;
    public isLoading: boolean;
    @ViewChild('fileImage') public fileImage: ElementRef;
    @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
    @ViewChild('sortSubs') public sortSubs: MatSort;

    public showWebcam = true;
    public allowCameraSwitch = true;
    public multipleWebcamsAvailable = false;
    public deviceId: string;
    public videoOptions: MediaTrackConstraints = {
        // width: {ideal: 1024},
        // height: {ideal: 576}
    };
    public errors: WebcamInitError[] = [];
    public webcamImage: WebcamImage = null;
    private readonly trigger: Subject<void> = new Subject<void>();
    private readonly nextWebcam: Subject<boolean | string> = new Subject<boolean | string>();



    constructor(private readonly vehicleService: VehicleService,
        private readonly userService: UserService,
        private readonly datePipe: DatePipe,
        private readonly breakDownService: BreakDownService,
        private readonly imageService: ImageService,
        private readonly propertyService: PropertyService,
        private readonly ngxService: NgxUiLoaderService,
        private readonly excelService: ExcelService,
        private readonly router: Router,
        public toastr: ToastrManager,
        private readonly formBuilder: FormBuilder) {
        this.pageIndex = 0;
        this.pageSize = 25;
        this.lowValue = 0;
        this.highValue = 5;
        this.vehicleForm = this.formBuilder.group({
            fleetNo: new FormControl(''),
            name: new FormControl(''),
            lastServiceDate: new FormControl(''),
            lastServiceHours: new FormControl(''),
            currentMachineHours: new FormControl(''),
            serviceIntervalHours: new FormControl(''),
            remainingHours: new FormControl(''),
            search: new FormControl(),
            usable: new FormControl(true)

        });
        this.breakDown = this.formBuilder.group({
            fleetNo: new FormControl(null),
            vehicleUUid: new FormControl(null),
            operator: [null, [Validators.required]],
            operatorUuid: [null],
            breakDownStartDate: [this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm:ss'), [Validators.required]],
            reportType: [null, [Validators.required]],
            currentKmHours: [null],
            userName: [null],
            desc: [null, [Validators.required]],
        });
        this.page = new Pageable();
        this.page.page = 0;
        this.propertyService.findByGroupName(this.page, 'ReportType').subscribe(data => {
            this.propertys = data.data.content;
        });
        this.breakDown.controls['breakDownStartDate'].disable();
        this.breakDown.controls['fleetNo'].disable();
        this.breakDown.controls['currentKmHours'].disable();
        this.breakDown.controls['userName'].disable();
    }

    public ngOnInit(): void {
        this.page = new Pageable();
        this.page.page = 0;
        this.page.size = 20;
        this.userService.filter(this.page, '1').subscribe(data => {
            this.et_users.data = data.data.content;
        });
        this.pageList(this.pageIndex, this.pageSize, true);
        this.breakDown.controls['operator'].valueChanges
            .pipe(
                debounceTime(100),
                tap(() => {
                    this.et_users.data = [];
                    this.isLoading = true;
                }),
                switchMap(value => this.userService.filter(this.page, value ? value : '*')
                    .pipe(
                        finalize(() => {
                            this.isLoading = false;
                        }),
                    )
                )
            )
            .subscribe(data => {
                this.et_users.data = data.data.content;
                if (data.data.content.length === 0) {
                    this.breakDown.get('operatorUuid').setValue('');
                    this.breakDown.get('userName').setValue('');
                }
            }, error => {
                this.et_users.data = [];
            });

    }

    public pageList = (page: number, size: number, first: boolean) => {
        this.page = new Pageable();
        this.page.page = page;
        this.page.size = size;
        this.vehicleFilter = new VehicleFilter();
        this.vehicleFilter.fleetNo = this.vehicleForm.value.fleetNo;
        this.vehicleFilter.vehicleTypeFilterForm = new VehicleTypeFilter();
        this.vehicleFilter.vehicleTypeFilterForm.name = this.vehicleForm.value.name;
        this.vehicleFilter.currentMachineHours = this.vehicleForm.value.currentMachineHours;
        this.vehicleFilter.serviceIntervalHours = this.vehicleForm.value.serviceIntervalHours;
        this.vehicleFilter.lastServiceHours = this.vehicleForm.value.lastServiceHours;
        this.vehicleFilter.remainingHours = this.vehicleForm.value.remainingHours;
        this.dataSource.data = [];
        this.vehicleService.filter(this.page, this.vehicleFilter).subscribe(data => {
            this.dataSource.data = data.data.content;
            this.dataSource.paginator = this.paginatorSubs;
            this.dataSource.sort = this.sortSubs;
            this.onLoading = true;
            this.totalElement = data.data.totalElements;
        });

    }
    public onEdit = (uuid) => {

        this.router.navigateByUrl('/vehicle/machine/' + uuid);
    }

    public onAdd(): void {

        this.router.navigateByUrl('/vehicle/machine');
    }

    public onView = (uuid) => {

        this.router.navigateByUrl('/vehicle/card/' + uuid);
    }

    public onDelete = (uuid) => {
        const _controller = this;
        swal({
            title: 'Are you sure you want to delete this Fleet ?',
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
                _controller.vehicleService.delete(uuid).subscribe(data => {
                    _controller.toastr.successToastr(data.text, 'Success!');
                    _controller.pageList(_controller.pageIndex, _controller.pageSize, true);
                });
            } else {
            }
        });
    }

    public ngAfterViewInit(): void {
        this.dataSource.sort = this.sortSubs;
        this.dataSource.paginator = this.paginatorSubs;
    }
    public showNotification(from: any, align: any): void {
        swal({
            title: 'DELETED',
            buttonsStyling: false,
            confirmButtonClass: 'btn btn-success'
        }).catch(swal.noop);
    }
    public serverdata = (event?: PageEvent) => {
        if (event.pageIndex === this.pageIndex + 1) {
            this.lowValue = this.lowValue + this.pageSize;
            this.highValue = this.highValue + this.pageSize;
        } else if (event.pageIndex === this.pageIndex - 1) {
            this.lowValue = this.lowValue - this.pageSize;
            this.highValue = this.highValue - this.pageSize;
        }
        this.pageIndex = event.pageIndex;
        this.pageSize = event.pageSize;
        this.pageList(event.pageIndex, event.pageSize, false);
        return event;

    }
    public applyFilter(prm?: string): void {
        this.dataSource.data = [];
        this.page = new Pageable();
        this.page.page = 0;
        this.page.size = this.pageSize;
        this.vehicleFilter = new VehicleFilter();
        this.vehicleFilter.fleetNo = this.vehicleForm.value.fleetNo;
        this.vehicleFilter.vehicleTypeFilterForm = new VehicleTypeFilter();
        this.vehicleFilter.vehicleTypeFilterForm.name = this.vehicleForm.value.name;
        this.vehicleFilter.currentMachineHours = this.vehicleForm.value.currentMachineHours;
        this.vehicleFilter.serviceIntervalHours = this.vehicleForm.value.serviceIntervalHours;
        this.vehicleFilter.lastServiceHours = this.vehicleForm.value.lastServiceHours;
        this.vehicleFilter.remainingHours = this.vehicleForm.value.remainingHours;
        if (prm === 'X') {
            this.page.size = 99999;
            this.vehicleService.postForFile(this.vehicleFilter).subscribe(data => {
                const a = document.createElement('a');
                a.href = URL.createObjectURL(data);
                a.download = 'VehicleList.xls';
                a.click();
            });
        } else {
            this.vehicleService.filter(this.page, this.vehicleFilter).subscribe(data => {
                this.dataSource.data = data.data.content;
                this.dataSource.paginator = this.paginatorSubs;
                this.dataSource.sort = this.sortSubs;
                this.onLoading = true;
                this.totalElement = data.data.totalElements;
            });
        }
    }
    public openBrekDown(vehicle: Vehicle, type: string): void {
        this.type = type;
        this.avatars = [];
        this.breakDown.get('fleetNo').setValue(vehicle.fleetNo);
        this.breakDown.get('currentKmHours').setValue(vehicle.currentMachineHours);
        this.breakDown.get('vehicleUUid').setValue(vehicle.uuid);
        this.breakDown.get('reportType').setValue(type);
        this.page.page = 0;
        this.page.size = 9999;
        this.userService.userGroup(this.page, 'OPERATOR').subscribe(data => {
            this.dataSourceUser.data = data.data.content;
        });
    }
    public onBreakDownSave(): void {
        const breakDownE = new BreakDown();
        if (!this.breakDown.valid) {
            this.toastr.errorToastr('', 'Required Field');
            return;
        }
        breakDownE.vehicleUuid = this.breakDown.controls['vehicleUUid'].value;
        breakDownE.operatorUuid = this.breakDown.value.operatorUuid;
        breakDownE.reportType = this.breakDown.value.reportType;
        breakDownE.description = this.breakDown.value.desc;
        const breakDownImage = new BreakDownImage();
        this.breakDownService.createBreakDown(breakDownE).subscribe(data => {
            this.toastr.successToastr('', 'Success');
            breakDownImage.imageInfos = this.imageList;
            breakDownImage.jobCardUuid = data.data.uuid;
            this.breakDownService.image(breakDownImage).subscribe(tm => {

            });
            $('#openBrekDown').modal('hide');
            this.breakDown.reset();
            this.pageList(this.pageIndex, this.pageSize, true);
        }, error => {
            this.toastr.errorToastr('', 'Error');
        });
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
            this.image.name = this.fileData.name;
            this.image.downloadUrl = data.data.downloadUrl;
            if (this.imageList.length === 0) {
                this.image.selected = true;
            } else {
                this.image.selected = false;
            }
            this.imageList.push(this.image);
            this.avatars.push(this.image);
            this.dataSourceImage.data = this.imageList;
            this.operationLock = false;
            this.ngxService.stop();

        });
    }
    public onImageDelete(uuid: string): void {
        this.ngxService.start();
        this.imageService.delete(uuid).subscribe(img => {
            this.ngxService.stop();

            this.avatars = this.avatars.filter(item => item.uuid !== uuid);
            this.toastr.successToastr('Image deleted', 'Success');
            this.avatars = this.avatars;
            if (this.avatars.length === 0) {
                this.image = new Image();
                this.image.downloadUrl = './assets/img/placeholder.jpg';
            }

        });
    }
    public displayFn(uuid?: string): string {
        if (this.et_users !== undefined) {
            this.et_users.data.forEach(item => {
                if (item.uuid = uuid) {
                    return item.staffNumber;
                }
            });
        } else {
            return null;

        }
    }
    public itemSelected(fieldName: string): void {
        this.et_users.data.forEach(item => {
            if (item.uuid === fieldName) {
                this.breakDown.get('operator').setValue(item.staffNumber);
                this.breakDown.get('operatorUuid').setValue(item.uuid);
                this.breakDown.get('userName').setValue(item.fullName);
                return;
            }
        });
    }
    public setStyle(it: any, item: Vehicle): string {
        if (item.serviceWarning === 'WARNING') {
            return 'warning';
        } else if (item.serviceWarning === 'ERROR') {
            return 'error';
        } else {
            if ((it % 2) === 0) {
                return 'zebra';
            } else {
                return '';
            }
        }
    }
    public capture(): void {
        $('#openBrekDown').modal('hide');
        $('#cameraModal').modal('show');
        this.toggleWebcam();
        this.cameraDialog = true;
        WebcamUtil.getAvailableVideoInputs()
            .then((mediaDevices: MediaDeviceInfo[]) => {
                this.multipleWebcamsAvailable = mediaDevices && mediaDevices.length > 1;
            });
    }
    public triggerSnapshot(): void {
        this.trigger.next();
    }

    public toggleWebcam(): void {
        this.showWebcam = !this.showWebcam;
    }

    public handleInitError(error: WebcamInitError): void {
        this.errors.push(error);
    }

    public showNextWebcam(directionOrDeviceId: boolean | string): void {
        // true => move forward through devices
        // false => move backwards through devices
        // string => move to device with given deviceId
        this.nextWebcam.next(directionOrDeviceId);
    }

    public handleImage(webcamImage: WebcamImage): void {
        this.webcamImage = webcamImage;
        this.ngxService.start();
        this.imageService.webcam(webcamImage).subscribe(data => {
            this.image = new Image();
            this.image.uuid = data.data.uuid;
            this.image.downloadUrl = data.data.downloadUrl;
            if (this.imageList.length === 0) {
                this.image.selected = true;
            } else {
                this.image.selected = false;
            }
            this.imageList.push(this.image);
            this.avatars.push(this.image);
            this.dataSourceImage.data = this.imageList;
            this.operationLock = false;
            this.ngxService.stop();
            this.toggleWebcam();
            $('#openBrekDown').modal('show');
            $('#cameraModal').modal('hide');


        });
    }

    public cameraWasSwitched(deviceId: string): void {
        this.deviceId = deviceId;
    }

    public get triggerObservable(): Observable<void> {
        return this.trigger.asObservable();
    }

    public get nextWebcamObservable(): Observable<boolean | string> {
        return this.nextWebcam.asObservable();
    }
}
