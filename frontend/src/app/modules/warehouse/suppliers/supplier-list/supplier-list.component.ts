import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, MatSort, MatTableDataSource, PageEvent} from '@angular/material';
import {Router} from '@angular/router';
import swal from 'sweetalert2';
import {SupplierService} from '../../../../services/warehouse/supplier.service';
import {Pageable} from '../../../../models/map/pagination';
import { SupplierForm } from 'src/app/models/suppliers/supplier-form';
import { FormGroup, FormBuilder, FormControl } from '@angular/forms';

@Component({
  selector: 'app-supplier-list',
  templateUrl: './supplier-list.component.html',
  styleUrls: ['./supplier-list.component.css']
})
export class SupplierListComponent  implements OnInit {

  public displayedColumns = ['name', 'address',
    'registerNumber', 'taxNumber', 'btn', 'excel' ];
  public dataSource = new MatTableDataSource<SupplierForm>();
  public page: Pageable;
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;
  public lowValue: number;
  public highValue: number;
  public onLoading: boolean;
  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;
  public form: FormGroup;

  constructor(private readonly supplierService: SupplierService,
              private readonly router: Router,
              private readonly formBuilder: FormBuilder) {
    this.pageIndex = 0;
    this.pageSize = 5;
    this.lowValue = 0;
    this.highValue = 5;
    this.form = this.formBuilder.group({
      search: new FormControl()
    });
  }

  public ngOnInit(): void {
    this.page = new Pageable();
    this.page.page = 0;
    this.supplierService.list(this.page).subscribe(data => {
      this.dataSource.data = data.data.content;
      this.dataSource.paginator = this.paginatorSubs;
      this.dataSource.sort = this.sortSubs;
      this.onLoading = true;
    });
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
    this.supplierService.list(this.page).subscribe(data => {
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
    this.router.navigateByUrl('/warehouse/supplier/' + uuid);
  }

  public onAdd(): void {

    this.router.navigateByUrl('/warehouse/supplier');
  }

  public onDelete = (uuid) => {
    const _controller = this;
    swal({
      title: 'Are you sure you want to delete this Supplier ?',
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
        _controller.supplierService.delete(uuid).subscribe(data => {
          _controller.showNotification('top', 'center');
          _controller.pageList(_controller.pageIndex, _controller.pageSize, true);
        });
      } else {
      }
    });
  }

  public ngAfterViewInit = (): void => {
    this.dataSource.sort = this.sortSubs;
    this.dataSource.paginator = this.paginatorSubs;

  }

  public showNotification = (from: any, align: any) => {
    swal({
      title: 'Deleted Successfully',
      buttonsStyling: false,
      confirmButtonClass: 'btn btn-success'
    }).catch(swal.noop);
  }
  public applyFilter(val: string): void {
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = this.pageSize;
    if (val.trim().length > 0) {
        this.supplierService.filter(this.page, val).subscribe(data => {
            this.dataSource.data = data.data.content;
            this.dataSource.paginator = this.paginatorSubs;
            this.dataSource.sort = this.sortSubs;
            this.onLoading = true;
            this.totalElement = data.data.totalElements;
        });
    } else if (val === 'X') {
      this.page.page = 0;
      this.page.size = 99999999;
      this.supplierService.postForFile(this.page, val).subscribe(data => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(data);
        a.download = 'SupplierList.xls';
        a.click();
    });
    } else {
        this.supplierService.list(this.page).subscribe(data => {
            this.dataSource.data = data.data.content;
            this.dataSource.paginator = this.paginatorSubs;
            this.dataSource.sort = this.sortSubs;
            this.onLoading = true;
            this.totalElement = data.data.totalElements;
        });
    }
}

}
