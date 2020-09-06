import { Component, OnInit } from '@angular/core';
import * as jspdf from 'jspdf';

import html2canvas from 'html2canvas';
import { BreakDownService } from 'src/app/services/workshop/break-down.service';
import { Router, ActivatedRoute } from '@angular/router';
import { JobCardInfo } from 'src/app/models/workshop/job-card/job-card-info';
import { JobCardInfoItem } from 'src/app/models/workshop/job-card/job-card-info-item';
import {SiteForm} from '../../../models/sites/site-form';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { Pageable } from 'src/app/models/map/pagination';

@Component({
  selector: 'app-job-card-pdf',
  templateUrl: './job-card-pdf.component.html',
  styleUrls: ['./job-card-pdf.component.scss']
})
export class JobCardPdfComponent implements OnInit {

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


  constructor(private readonly breakDownService: BreakDownService,
    private readonly activatedRouter: ActivatedRoute,
    public readonly formBuilder: FormBuilder,
    private readonly router: Router) {
    this.title = 'JOB CARD';
    this.sumRow = 20;
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
    this.form.controls['riskAssessment'].disable();
    this.form.controls['lockOutProcedure'].disable();
    this.form.controls['wheelNuts'].disable();
    this.form.controls['oilLevel'].disable();
    this.form.controls['machineGrease'].disable();

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
    this.router.navigateByUrl('/workshop/list');
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
      pdf.save(this.jobCardInfo.requestNumber + '.pdf');
      this.showPdfDiv = false;
    });
  }


}
