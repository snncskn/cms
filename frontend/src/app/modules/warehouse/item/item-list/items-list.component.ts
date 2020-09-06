import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { VehicleService } from 'src/app/services/vehicle/vehicle.service';
import { Router } from '@angular/router';
import { MatTableDataSource, MatSort, MatPaginator, PageEvent } from '@angular/material';
import swal from 'sweetalert2';
import { Pageable } from 'src/app/models/map/pagination';
import { FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { ItemService } from 'src/app/services/item/item.service';
import { PartItem } from 'src/app/models/item/item';
import { ToastrManager } from 'ng6-toastr-notifications';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { ExcelService } from 'src/app/services/general/excel.service';
import { ItemFilterForm } from 'src/app/models/item/item-fliter';

declare const $: any;

@Component({
    selector: 'app-items-list',
    templateUrl: './items-list.component.html',
    styleUrls: ['./items-list.component.css'],
    providers: [VehicleService]
})

export class ItemsListComponent implements OnInit, AfterViewInit {

    public displayedColumns = ['storePartNumber',
        'barcode', 'itemDescription',
        'minStockQuantity', 'price', 'currentQuantity', 'btn', 'excel'];
    public dataSource = new MatTableDataSource<PartItem>();
    public page: Pageable;
    public totalElement: number;
    public pageIndex: number;
    public pageSize: number;
    public lowValue: number;
    public highValue: number;
    public itemForm: FormGroup;
    public itemFilter: ItemFilterForm;

    public onLoading: boolean;
    @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
    @ViewChild('sortSubs') public sortSubs: MatSort;

    constructor(private readonly itemService: ItemService,
        private readonly router: Router,
        private readonly formBuilder: FormBuilder,
        private readonly ngxService: NgxUiLoaderService,
        private readonly excelService: ExcelService,
        public toastr: ToastrManager) {
        this.pageIndex = 0;
        this.pageSize = 25;
        this.lowValue = 0;
        this.highValue = 25;
        this.itemForm = this.formBuilder.group({
            storePartNumber: [''],
            barcode: [''],
            itemDescription: [''],
        });
    }
    public ngOnInit(): void {
        this.page = new Pageable();
        this.page.page = 0;
        this.pageList(this.pageIndex, this.pageSize, true);

    }
    public pageList = (page: number, size: number, first: boolean) => {
        this.ngxService.start();
        this.page = new Pageable();
        this.page.page = page;
        this.page.size = size;
        this.itemService.list(this.page).subscribe(data => {
            this.dataSource.data = data.data.content;
            if (first) {
                this.dataSource.paginator = this.paginatorSubs;
                this.dataSource.sort = this.sortSubs;
            }
            this.onLoading = true;
            this.totalElement = data.data.totalElements;
            this.ngxService.stop();

        }, error => {

        });
    }
    public onEdit = (uuid) => {
        this.router.navigateByUrl('/warehouse/item/' + uuid);
    }
    public onAdd(): void {
        this.router.navigateByUrl('/warehouse/item');
    }
    public onDelete(uuid: string): void {
        const _controller = this;
        swal({
          title: 'Are you sure you want to delete this Item ?',
          html: '<div class=“form-group”>' +
            '</div>',
          showCancelButton: true,
          confirmButtonClass: 'btn btn-success',
          confirmButtonText: 'Yes',
          cancelButtonText: 'No',
          cancelButtonClass: 'btn btn-danger',
          buttonsStyling: false
        }).then( (result) => {
            if (result.value) {
                _controller.itemService.delete(uuid).subscribe(data => {
                    _controller.toast(data.type, data.text);
                    _controller.router.navigateByUrl('/warehouse/items');
                    _controller.pageList(_controller.pageIndex, _controller.pageSize, true);
                }, error => {
                    _controller.toast(error.error.type, error.error.text);
                });
            } else {

            }
        });
    }
    public ngAfterViewInit(): void {
        this.dataSource.sort = this.sortSubs;
        this.dataSource.paginator = this.paginatorSubs;

    }

    public showNotification = (from: any, align: any) => {
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
        } else {

        }
        this.pageIndex = event.pageIndex;
        this.pageList(event.pageIndex, event.pageSize, false);
        return event;
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
    public applyFilter = (prm?: string) => {
        this.page = new Pageable();
        this.page.page = 0;
        this.page.size = this.pageSize;

        this.itemFilter = new ItemFilterForm();
        this.itemFilter.storePartNumber = this.itemForm.value.storePartNumber;
        this.itemFilter.itemDescription = this.itemForm.value.itemDescription;
        this.itemFilter.barcode = this.itemForm.value.barcode;
        this.itemFilter.filter =  this.itemForm.value.storePartNumber;
        this.itemFilter.page = 0;
        this.itemFilter.size = this.pageSize;

        if (prm === 'X') {
            this.ngxService.start();
            this.itemService.xls(this.itemFilter).subscribe(data => {
                const a = document.createElement('a');
                a.href = URL.createObjectURL(data);
                a.download = 'Items.xls';
                a.click();
                this.ngxService.stop();
            });
        } else {
            this.itemService.filterPost(this.itemFilter).subscribe(data => {
                this.dataSource.data = data.data.content;
                this.dataSource.paginator = this.paginatorSubs;
                this.dataSource.sort = this.sortSubs;
                this.onLoading = true;
                this.totalElement = data.data.totalElements;
                this.ngxService.stop();
            });
        }
    }
    public setStyle(item: PartItem, it: any): string {
        if (item.minStockQuantity > item.currentQuantity) {
            return 'warning';
        } else {
            if ((it % 2) === 0) {
                return 'zebra';
            } else {
                return '';
            }
        }
    }

}
