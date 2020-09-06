import { Component, OnInit, AfterViewInit, ViewChild, Input } from '@angular/core';
import { VehicleService } from 'src/app/services/vehicle/vehicle.service';
import { Router } from '@angular/router';
import { MatPaginator, MatSort, MatTableDataSource, MatDialog, MatSnackBar, VERSION, PageEvent }
from '@angular/material';
import { FormControl, FormGroup } from '@angular/forms';
import { AttributesService } from '../../../../services/vehicle/attributes.service';
import swal from 'sweetalert2';
import { Pageable } from 'src/app/models/map/pagination';
import { VehicleTypeCreateUpdateForm } from 'src/app/models/vehicle/vehicle-type-create-update-form';
import { ItemTypeService } from 'src/app/services/item/item-type.service';
import { ToastrManager } from 'ng6-toastr-notifications';

declare const $: any;

@Component({
    selector: 'app-items-type',
    templateUrl: './items-type.component.html',
    styleUrls: ['./items-type.component.css'],
    providers: [VehicleService, AttributesService]
})


export class ItemsTypeListComponent implements OnInit, AfterViewInit {

    public displayedColumns = ['name', 'btn'];
    public dataSource = new MatTableDataSource<VehicleTypeCreateUpdateForm>();
    public page: Pageable;
    public totalElement: number;
    public pageIndex: number;
    public pageSize: number;
    public lowValue: number;
    public highValue: number;
    @Input() public pageSizeOptions: number[] = [10, 25, 50, 100];


    public onLoading: boolean;
    @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
    @ViewChild('sortSubs') public sortSubs: MatSort;

    public frmGroup = new FormGroup({
        search: new FormControl(),

    });

    constructor(private readonly itemTypeService: ItemTypeService,
                private readonly router: Router,
                public toastr: ToastrManager) {
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

    public getServerData = (event?: PageEvent) => {

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

    public pageList = (page: number, size: number, first: boolean) => {

        this.page = new Pageable();
        this.page.page = page;
        this.page.size = size;
        this.itemTypeService.list(this.page).subscribe(data => {

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

        this.router.navigateByUrl('/settings/item-type/' + uuid);
    }

    public onAdd(): void {

        this.router.navigateByUrl('/settings/item-type');
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
        }).then(() => {
                _controller.itemTypeService.delete(uuid).subscribe(data => {
                   // _controller.showNotification('top', 'center');
                    _controller.pageList(_controller.pageIndex, _controller.pageSize, true);
                    _controller.toast(data.type, data.text);
                }, error => {
                    _controller.toast(error.error.type, error.error.text);


                });
        });


    }
    public showNotification = (from: any, align: any) => {

        swal({
            title: 'Deleted Successfully',
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
      public applyFilter(val: string): void {
        this.page = new Pageable();
        this.page.page = 0;
        this.page.size = this.pageSize;
        if (val.trim().length > 0) {
            this.itemTypeService.filter(this.page, val).subscribe(data => {
                this.dataSource.data = data.data.content;
                this.dataSource.paginator = this.paginatorSubs;
                this.dataSource.sort = this.sortSubs;
                this.onLoading = true;
                this.totalElement = data.data.totalElements;
            });
        } else {
            this.itemTypeService.list(this.page).subscribe(data => {
                this.dataSource.data = data.data.content;
                this.dataSource.paginator = this.paginatorSubs;
                this.dataSource.sort = this.sortSubs;
                this.onLoading = true;
                this.totalElement = data.data.totalElements;
            });
        }
    }
}
