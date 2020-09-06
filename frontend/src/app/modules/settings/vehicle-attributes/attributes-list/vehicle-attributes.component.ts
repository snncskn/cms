import { Component, OnInit, AfterViewInit, ViewChild, Input } from '@angular/core';
import { VehicleService } from 'src/app/services/vehicle/vehicle.service';
import { Router } from '@angular/router';
import { MatPaginator, MatSort, MatTableDataSource, PageEvent } from '@angular/material';
import { FormControl, FormGroup } from '@angular/forms';
import { AttributesService } from '../../../../services/vehicle/attributes.service';
import swal from 'sweetalert2';
import { Pageable } from 'src/app/models/map/pagination';
import { VehicleAttribute } from 'src/app/models/map/pagination-attribute';
import { ToastrManager } from 'ng6-toastr-notifications';
declare const $: any;
@Component({
  selector: 'app-attributes',
  templateUrl: './vehicle-attributes.component.html',
  styleUrls: ['./vehicle-attributes.component.css'],
  providers: [VehicleService]
})
export class VehicleAttributesComponent implements OnInit, AfterViewInit {
  public displayedColumns = ['name', 'btn'];
  public dataSource = new MatTableDataSource<VehicleAttribute>();
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;
  public lowValue: number;
  public highValue: number;
  public page: Pageable;
  public onLoading: boolean;
  @Input() public pageSizeOptions: number[] = [5, 10, 25, 50, 100];
  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;
  public frmGroup = new FormGroup({
    search: new FormControl(),
  });
  constructor(private readonly attrService: AttributesService,
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
  public onEdit = (uuid) => {
    this.router.navigateByUrl('/settings/vehicle-attribute/' + uuid);
  }
  public onAdd(): void {

    this.router.navigateByUrl('/settings/vehicle-attribute');
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
        _controller.attrService.delete(uuid).subscribe(data => {
          _controller.toast(data.type, data.text);
          _controller.pageList(_controller.pageIndex, _controller.pageSize, true);

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
  public pageList(page: number, size: number, first: boolean): void {
    this.page = new Pageable();
    this.page.page = page;
    this.page.size = size;
    this.attrService.list(this.page).subscribe(data => {
      this.dataSource.data = data.data.content;
      if (first) {
        this.dataSource.paginator = this.paginatorSubs;
        this.dataSource.sort = this.sortSubs;
      }
      this.onLoading = true;
      this.totalElement = data.data.totalElements;
    });
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
        this.attrService.filter(this.page, val).subscribe(data => {
            this.dataSource.data = data.data.content;
            this.dataSource.paginator = this.paginatorSubs;
            this.dataSource.sort = this.sortSubs;
            this.onLoading = true;
            this.totalElement = data.data.totalElements;
        });
    } else {
        this.attrService.list(this.page).subscribe(data => {
            this.dataSource.data = data.data.content;
            this.dataSource.paginator = this.paginatorSubs;
            this.dataSource.sort = this.sortSubs;
            this.onLoading = true;
            this.totalElement = data.data.totalElements;
        });
    }
}
}
