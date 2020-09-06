import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatPaginator, MatSort, MatTableDataSource, PageEvent } from '@angular/material';
import { FormControl, FormGroup } from '@angular/forms';
import swal from 'sweetalert2';
import { Pageable } from 'src/app/models/map/pagination';
import {SiteService} from '../../../../services/general/site.service';
import { SiteForm } from 'src/app/models/sites/site-form';

declare const $: any;

@Component({
  selector: 'app-sites-list',
  templateUrl: './sites-list.component.html',
  styleUrls: ['./sites-list.component.css']
})

export class SitesListComponent implements OnInit, AfterViewInit {

  public displayedColumns = ['description', 'btn'];
  public dataSource = new MatTableDataSource<SiteForm>();
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
  constructor(private readonly siteService: SiteService,
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
    this.siteService.list(this.page).subscribe(data => {
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
    this.router.navigateByUrl('/settings/site/' + uuid);
  }
  public onAdd(): void {
    this.router.navigateByUrl('/settings/site');
  }
  public onDelete = (uuid) => {
    const _controller = this;
    swal({
      title: 'Are you sure you want to delete this Site ?',
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
        _controller.siteService.delete(uuid).subscribe(data => {
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
        this.siteService.filter(this.page, val).subscribe(data => {
            this.dataSource.data = data.data.content;
            this.dataSource.paginator = this.paginatorSubs;
            this.dataSource.sort = this.sortSubs;
            this.onLoading = true;
            this.totalElement = data.data.totalElements;
        });
    } else {
        this.siteService.list(this.page).subscribe(data => {
            this.dataSource.data = data.data.content;
            this.dataSource.paginator = this.paginatorSubs;
            this.dataSource.sort = this.sortSubs;
            this.onLoading = true;
            this.totalElement = data.data.totalElements;
        });
    }
}
}
