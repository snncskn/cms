import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { VehicleService } from 'src/app/services/vehicle/vehicle.service';
import { Router } from '@angular/router';
import { VehicleTypeService } from '../../../../services/vehicle/vehicle-type.service';
import { MatPaginator, MatSort, MatTableDataSource, PageEvent } from '@angular/material';
import { FormControl, FormGroup } from '@angular/forms';
import { AttributesService } from '../../../../services/vehicle/attributes.service';
import swal from 'sweetalert2';
import { Pageable } from 'src/app/models/map/pagination';
import { VehicleTypeCreateUpdateForm } from 'src/app/models/vehicle/vehicle-type-create-update-form';
import { ToastrManager } from 'ng6-toastr-notifications';

declare const $: any;

@Component({
    selector: 'app-vehicle-type',
    templateUrl: './vehicle-type.component.html',
    styleUrls: ['./vehicle-type.component.css'],
    providers: [VehicleService, AttributesService]
})

export class VehicleTypeListComponent implements OnInit, AfterViewInit {

    public displayedColumns = ['name', 'btn'];
    public dataSource = new MatTableDataSource<VehicleTypeCreateUpdateForm>();
    public page: Pageable;
    public totalElement: number;
    public pageIndex: number;
    public pageSize: number;
    public lowValue: number;
    public highValue: number;
    public onLoading: boolean;
    @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
    @ViewChild('sortSubs') public sortSubs: MatSort;
    public frmGroup = new FormGroup({
        search: new FormControl(),
    });
    constructor(private readonly vehicleTypeService: VehicleTypeService,
        public toastr: ToastrManager,
        private readonly router: Router) {
        this.pageIndex = 0;
        this.pageSize = 25;
        this.lowValue = 0;
        this.highValue = 25;
    }
    public ngOnInit(): void {
        this.pageList(this.pageIndex, this.pageSize, true);
    }
    public ngAfterViewInit(): void {
        this.dataSource.sort = this.sortSubs;
        this.dataSource.paginator = this.paginatorSubs;
    }
    public getServerData(event?: PageEvent): any {
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
    public pageList(page: number, size: number, first: boolean): void {
        this.page = new Pageable();
        this.page.page = page;
        this.page.size = size;
        this.vehicleTypeService.list(this.page).subscribe(data => {
            this.dataSource.data = data.data.content;
            if (first) {
                this.dataSource.paginator = this.paginatorSubs;
                this.dataSource.sort = this.sortSubs;
            }
            this.onLoading = true;
            this.totalElement = data.data.totalElements;
        });
    }
    public onEdit = (uuid) => {
        this.router.navigateByUrl('/settings/vehicle-type/' + uuid);
    }
    public onAdd(): void {
        this.router.navigateByUrl('/settings/vehicle-type');
    }
    public onDelete = (uuid) => {
        const _controller = this;
        swal({
            title: 'Are you sure you want to delete ?',
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
                _controller.vehicleTypeService.delete(uuid).subscribe(data => {
                    _controller.toastr.successToastr(data.msgId, 'Success!');
                    _controller.pageList(_controller.pageIndex, _controller.pageSize, true);
                });
            } else {
            }
        });
    }

    public applyFilter(val: string): void {
        this.page = new Pageable();
        this.page.page = 0;
        this.page.size = this.pageSize;
        if (val.trim().length > 0) {
            this.vehicleTypeService.filter(this.page, val).subscribe(data => {
                this.dataSource.data = data.data.content;
                this.dataSource.paginator = this.paginatorSubs;
                this.dataSource.sort = this.sortSubs;
                this.onLoading = true;
                this.totalElement = data.data.totalElements;
            });
        } else {
            this.vehicleTypeService.list(this.page).subscribe(data => {
                this.dataSource.data = data.data.content;
                this.dataSource.paginator = this.paginatorSubs;
                this.dataSource.sort = this.sortSubs;
                this.onLoading = true;
                this.totalElement = data.data.totalElements;
            });
        }
    }
}
