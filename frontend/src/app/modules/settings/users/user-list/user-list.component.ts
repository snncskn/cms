import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {UserService} from '../../../../services/general/user.service';
import {MatPaginator, MatSort, MatTableDataSource, PageEvent} from '@angular/material';
import {Pageable} from '../../../../models/map/pagination';
import {FormBuilder, FormGroup, FormControl} from '@angular/forms';
import {Router} from '@angular/router';
import {ToastrManager} from 'ng6-toastr-notifications';
import swal from 'sweetalert2';
import {UserForm} from '../../../../models/users/userForm';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css'],
  providers: [UserService]
})
export class UserListComponent implements OnInit {
  public displayedColumns = ['firstName', 'lastName',
    'email', 'contactNumber', 'staffNumber', 'expireDate', 'btn'];
  public dataSource = new MatTableDataSource<UserForm>();
  public page: Pageable;
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;
  public lowValue: number;
  public highValue: number;
  public userForm: FormGroup;
  public onLoading: boolean;

  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;

  constructor(private readonly userService: UserService,
              private readonly router: Router,
              public toastr: ToastrManager,
              private readonly formBuilder: FormBuilder) {
    this.pageIndex = 0;
    this.pageSize = 25;
    this.lowValue = 0;
    this.highValue = 25;
    this.userForm = this.formBuilder.group({
      search: new FormControl()
    });
  }

  public ngOnInit(): void {
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = this.pageSize;
    this.userService.list(this.page).subscribe(data => {
      this.dataSource.data = data.data.content;
      this.dataSource.paginator = this.paginatorSubs;
      this.dataSource.sort = this.sortSubs;
      this.onLoading = true;
    });
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
    this.userService.list(this.page).subscribe(data => {
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

    this.router.navigateByUrl('/settings/user/' + uuid);
  }

  public onAdd(): void {

    this.router.navigateByUrl('/settings/user');
  }

  public onView = (uuid) => {

    this.router.navigateByUrl('/settings/user/' + uuid);
  }

  public onDelete = (uuid) => {
    const _controller = this;
    swal({
      title: 'Are you sure you want to delete this User ?',
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
        _controller.userService.delete(uuid).subscribe(data => {
          _controller.toastr.successToastr(data.text, 'Success!');
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
      title: 'DELETED',
      buttonsStyling: false,
      confirmButtonClass: 'btn btn-success'
    }).catch(swal.noop);
  }

  public applyFilter(val: string): void {
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = this.pageSize;
    if (val.trim().length > 0 ) {
      this.userService.filter(this.page, val).subscribe(data => {

        this.dataSource.data = data.data.content;
        this.dataSource.paginator = this.paginatorSubs;
        this.dataSource.sort = this.sortSubs;
        this.onLoading = true;
        this.totalElement = data.data.totalElements;
      });
    } else {
      this.userService.list(this.page).subscribe(data => {

        this.dataSource.data = data.data.content;
        this.dataSource.paginator = this.paginatorSubs;
        this.dataSource.sort = this.sortSubs;
        this.onLoading = true;
        this.totalElement = data.data.totalElements;
      });
    }
  }
}
