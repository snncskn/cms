import { Component, OnInit, Input } from '@angular/core';
import { OrderService } from 'src/app/services/warehouse/order.service';
import { Order } from 'src/app/models/purchase/order';
import * as jspdf from 'jspdf';

import html2canvas from 'html2canvas';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from 'src/app/services/general/authentication.service';
import { SupplierForm } from 'src/app/models/suppliers/supplier-form';
import { Image } from 'src/app/models/general/image';

@Component({
  selector: 'app-purchase-pdf',
  templateUrl: './purchase-pdf.component.html',
  styleUrls: ['./purchase-pdf.component.css']
})
export class PurchasePdfComponent implements OnInit {

  public showPdfDiv: boolean;
  public order: Order;
  public image: Image;
  public sumRow: number;
  public rows: string[];
  public title: string;
  public mySupplier: SupplierForm;
  public showDiv: boolean;
  constructor(private readonly orderService: OrderService,
    private readonly router: Router,
    private readonly authenticationService: AuthenticationService,
    private readonly activatedRouter: ActivatedRoute
  ) {
    this.sumRow = 50;
    this.showDiv = false;
    this.rows = [];
    this.showPdfDiv = false;
    this.mySupplier = new SupplierForm();
    if (this.authenticationService.currentUserValue.currentSite.supplierInfo !== null) {
      this.mySupplier.name = this.authenticationService.currentUserValue.currentSite.supplierInfo.name;
      this.mySupplier.address = this.authenticationService.currentUserValue.currentSite.supplierInfo.address;
      this.mySupplier.taxNumber = this.authenticationService.currentUserValue.currentSite.supplierInfo.taxNumber;
    }
  }

  public ngOnInit(): void {
    this.activatedRouter.paramMap.subscribe(params => {
      if (params.has('id')) {
        this.orderService.find(params.get('id')).subscribe(data => {
          this.showDiv = true;

          this.order = data.data;
          data.data.supplierInfo.imageInfos.forEach(i => {
            if (i.selected) {
              this.image = i;
            }
          });
          if (this.order.status === 'PURCHASE_REQUEST') {
            this.title = 'PURCHASE REQUEST';
          } else if (this.order.status === 'ORDER') {
            this.title = 'PURCHASE ORDER';
          } else if (this.order.status === 'INVOICE') {
            this.title = 'TAX INVOICE';
          }
          for (let i = 0; i < (this.sumRow - data.data.orderItemInfos.length); i++) {
            this.rows.push('');
          }
        });
        this.showPdfDiv = true;

      }
    });

  }
  public pdf(): void {
    this.showPdfDiv = true;
    const data = document.getElementById('contentToConvert');
    html2canvas(data).then(canvas => {
      const imgWidth = 208;
      const pageHeight = 295;
      const imgHeight = canvas.height * imgWidth / canvas.width;
      const heightLeft = imgHeight;

      const contentDataURL = canvas.toDataURL('image/jpeg');
      const pdf = new jspdf('p', 'mm', 'a4');
      const position = 0;
      pdf.addImage(contentDataURL, 'JPEG', 0, position, imgWidth, imgHeight);
      pdf.save(this.order.requestNumber);
      this.showPdfDiv = false;
      this.onBack();
    });
  }
  public onBack(): void {
    this.router.navigateByUrl('/warehouse/purchase/' + this.order.uuid);
  }

}
