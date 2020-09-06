import {Component, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import swal from 'sweetalert2';
import {ToastrManager} from 'ng6-toastr-notifications';
import {Router} from '@angular/router';
import {Property} from '../../../../models/property/property';
import {Pageable} from '../../../../models/map/pagination';
import {PageEvent} from '@angular/material/typings/esm5/paginator';
import {PropertyService} from '../../../../services/general/property.service';
import { PropertyForm } from 'src/app/models/property/property-form';

@Component({
  selector: 'app-property-list',
  templateUrl: './property-list.component.html',
  styleUrls: ['./property-list.component.scss']
})
export class PropertyListComponent implements OnInit {
  public displayedColumns = ['groupName', 'keyLabel',
    'keyValue', 'btn'];
  public dataSource = new MatTableDataSource<PropertyForm>();
  public page: Pageable;
  public totalElement: number;
  public pageIndex: number;
  public pageSize: number;
  public lowValue: number;
  public highValue: number;
  public propertyForm: FormGroup;
  public onLoading: boolean;
  @ViewChild('paginatorSubs') public paginatorSubs: MatPaginator;
  @ViewChild('sortSubs') public sortSubs: MatSort;

  constructor(private readonly propertyService: PropertyService,
              private readonly router: Router,
              public toastr: ToastrManager,
              private readonly formBuilder: FormBuilder) {
    this.pageIndex = 0;
    this.pageSize = 5;
    this.lowValue = 0;
    this.highValue = 5;
    this.propertyForm = this.formBuilder.group({
      search: new FormControl(),
      usable: new FormControl(true)

    });
  }

  public ngOnInit(): void {
    this.page = new Pageable();
    this.page.page = 0;
    this.pageList(this.pageIndex, this.pageSize, true);
  }

  public pageList = (page: number, size: number, first: boolean) => {

    this.page = new Pageable();
    this.page.page = page;
    this.page.size = size;
    this.propertyService.list(this.page).subscribe(data => {

      this.dataSource.data = data.data.content;
      if (this.propertyForm.value.search != null ) {
        this.propertyService.filter(this.page, this.propertyForm.value.search).subscribe(item => {

          this.dataSource.data = item.data.content;
          if (first) {
            this.dataSource.paginator = this.paginatorSubs;
            this.dataSource.sort = this.sortSubs;

          }
          this.onLoading = true;
          this.totalElement = item.data.totalElements;
        });
      } else {
        this.propertyService.list(this.page).subscribe(itm => {

          this.dataSource.data = itm.data.content;
          if (first) {
            this.dataSource.paginator = this.paginatorSubs;
            this.dataSource.sort = this.sortSubs;

          }
          this.onLoading = true;
          this.totalElement = itm.data.totalElements;
        });
      }
    });
  }
  public onEdit = (uuid) => {

    this.router.navigateByUrl('/settings/property/' + uuid);
  }

  public onAdd(): void {

    this.router.navigateByUrl('/settings/property');
  }

  public onView = (uuid) => {

    // this.router.navigateByUrl('/vehicle/card/' + uuid);
  }

  public onDelete = (uuid) => {
    const _controller = this;
    swal({
      title: 'Are you sure you want to delete?',
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
        _controller.propertyService.delete(uuid).subscribe(data => {
          _controller.toastr.successToastr(data.text, 'Success!');
          _controller.pageList(_controller.pageIndex, _controller.pageSize, true);
        });
      } else {
      }
    });
  }

  // public ngAfterViewInit(): void {
  //   this.dataSource.sort = this.sortSubs;
  //   this.dataSource.paginator = this.paginatorSubs;
  // }

  public showNotification(from: any, align: any): void {
    swal({
      title: 'DELETED',
      buttonsStyling: false,
      confirmButtonClass: 'btn btn-success'
    }).catch(swal.noop);
  }
  public serverdata = (event?: PageEvent) => {
    if (event.pageIndex === this.pageIndex + 2) {
      this.lowValue = this.lowValue + this.pageSize;
      this.highValue = this.highValue + this.pageSize;
    } else if (event.pageIndex === this.pageIndex - 2) {
      this.lowValue = this.lowValue - this.pageSize;
      this.highValue = this.highValue - this.pageSize;
    } else {
      this.pageIndex = event.pageIndex;
      this.pageList(event.pageIndex, event.pageSize, false);
      return event;
    }
  }
  public applyFilter(val: string): void {
    this.page = new Pageable();
    this.page.page = 0;
    this.page.size = this.pageSize;
    if (val.trim().length > 0) {
      this.propertyService.filter(this.page, val).subscribe(data => {
        this.dataSource.data = data.data.content;
        this.dataSource.paginator = this.paginatorSubs;
        this.dataSource.sort = this.sortSubs;
        this.onLoading = true;
        this.totalElement = data.data.totalElements;
      });
    } else {
      this.propertyService.list(this.page).subscribe(data => {
        this.dataSource.data = data.data.content;
        this.dataSource.paginator = this.paginatorSubs;
        this.dataSource.sort = this.sortSubs;
        this.onLoading = true;
        this.totalElement = data.data.totalElements;
      });
    }
  }
  public onChange(): void {

  }

}
