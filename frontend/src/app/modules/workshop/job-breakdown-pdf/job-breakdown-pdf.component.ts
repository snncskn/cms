import { Component, OnInit } from '@angular/core';
import {JobCardInfo} from '../../../models/workshop/job-card/job-card-info';
import {JobCardInfoItem} from '../../../models/workshop/job-card/job-card-info-item';
import {SiteForm} from '../../../models/sites/site-form';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {BreakDownService} from '../../../services/workshop/break-down.service';
import {ActivatedRoute, Router} from '@angular/router';
import html2canvas from 'html2canvas';
import * as jspdf from 'jspdf';
import { AuthenticationService } from 'src/app/services/general/authentication.service';
import { User } from 'src/app/models/general/user';
import { Workshop } from '../workshop.module';
import { Location } from '@angular/common';
import { Pageable } from 'src/app/models/map/pagination';



@Component({
  selector: 'app-job-breakdown-pdf',
  templateUrl: './job-breakdown-pdf.component.html',
  styleUrls: ['./job-breakdown-pdf.component.css']
})
export class JobBreakdownPdfComponent implements OnInit {

  public title: string;
  public description: string;


  public rows: string[];
  public jobCardInfo: JobCardInfo;
  public sumRow: number;
  public showPdfDiv: boolean;
  public page: Pageable;
  public jobCardInfoItem: JobCardInfoItem[];
  public site: SiteForm;
  public form: FormGroup;
  public user: User;
  public workshop: Workshop;


  constructor(private readonly breakDownService: BreakDownService,
              private readonly activatedRouter: ActivatedRoute,
              private readonly authenticationService: AuthenticationService,
              public readonly formBuilder: FormBuilder,
              private readonly router: Router,
              private readonly location: Location) {
    this.title = 'BREAKDOWN REPORT';
    this.sumRow = 30;
    this.rows = [];
    this.jobCardInfo = new JobCardInfo();
    this.site = new SiteForm();
    this.site.description = 'HAKANO';

    this.form = formBuilder.group({
      riskAssessment: new FormControl(false, [Validators.required]),
      lockOutProcedure: new FormControl(false, [Validators.required]),
      wheelNuts: new FormControl(false, [Validators.required]),
      oilLevel: new FormControl(false, [Validators.required]),
      machineGrease: new FormControl(false, [Validators.required]),
    });

    this.user = this.authenticationService.currentUserValue;

  }

  public ngOnInit(): void {
    this.activatedRouter.paramMap.subscribe(params => {
      this.breakDownService.findJobCard(params.get('id')).subscribe(data => {
        this.jobCardInfo = data.data;
        this.form.patchValue(data.data);
      });
      this.page = new Pageable();
      this.page.page = 0;
      this.page.size = 9999;
      this.breakDownService.findJobCardItem(params.get('id'), this.page).subscribe(item => {
        this.jobCardInfoItem = item.data.content;
        for (let i = 0; i < (this.sumRow - item.data.content.length); i++) {
          this.rows.push('');
        }
      });
    });
  }
  public onBack(): void {
    // this.router.navigateByUrl('/workshop/list');
    this.location.back();
  }
  public pdf(): void {
    this.showPdfDiv = true;
    const data = document.getElementById('contentToConvert');
    html2canvas(data).then(canvas => {
      const imgWidth = 210;
      const pageHeight = 295;
      const imgHeight = canvas.height * imgWidth / canvas.width;
      const heightLeft = imgHeight;

      const contentDataURL = canvas.toDataURL('image/jpeg');
      const pdf = new jspdf('p', 'mm', 'a4');
      const position = 0;
      pdf.addImage(contentDataURL, 'JPEG', 0, position, imgWidth, imgHeight);
      pdf.save(this.jobCardInfo.requestNumber + '.pdf');
      this.showPdfDiv = false;
    });
  }
  public setStyle(it: any): string {
    if ((it % 2) === 0) {
        return 'zebra';
    } else {
        return '';
    }
}

}
