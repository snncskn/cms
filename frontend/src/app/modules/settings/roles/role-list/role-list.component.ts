import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, MatSort, MatTableDataSource, PageEvent} from '@angular/material';
import {Pageable} from '../../../../models/map/pagination';
import {FormControl, FormGroup} from '@angular/forms';
import {Router} from '@angular/router';
import {RoleCreateUpdateForm} from '../../../../models/roles/role-create-update-form';
import {RoleService} from '../../../../services/role/role.service';
import swal from 'sweetalert2';

@Component({
  selector: 'app-role-list',
  templateUrl: './role-list.component.html',
  styleUrls: ['./role-list.component.scss']
})
export class RoleListComponent implements OnInit, AfterViewInit {

  public displayedColumns = ['name', 'btn'];
  public dataSource = new MatTableDataSource<RoleCreateUpdateForm>();
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
  constructor(private readonly roleService: RoleService,
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
    this.roleService.list(this.page).subscribe(data => {
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
    this.router.navigateByUrl('/settings/role/' + uuid);
  }
  public onAdd(): void {
    this.router.navigateByUrl('/settings/role');
  }
  public onDelete = (uuid) => {
    const _controller = this;
    swal({
      title: 'Are you sure you want to delete this Role ?',
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
        _controller.roleService.delete(uuid).subscribe(data => {
          _controller.showNotification('top', 'center');
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
  public applyFilter(val: string): void {
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = this.pageSize;
    if (val.trim().length > 0) {
      this.roleService.filter(this.page, val).subscribe(data => {
        this.dataSource.data = data.data.content;
        this.dataSource.paginator = this.paginatorSubs;
        this.dataSource.sort = this.sortSubs;
        this.onLoading = true;
        this.totalElement = data.data.totalElements;
      });
    } else {
      this.roleService.list(this.page).subscribe(data => {
        this.dataSource.data = data.data.content;
        this.dataSource.paginator = this.paginatorSubs;
        this.dataSource.sort = this.sortSubs;
        this.onLoading = true;
        this.totalElement = data.data.totalElements;
      });
    }
  }
}

