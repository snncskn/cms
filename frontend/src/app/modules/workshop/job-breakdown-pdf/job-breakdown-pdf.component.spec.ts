import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { JobBreakdownPdfComponent } from './job-breakdown-pdf.component';

describe('JobBreakdownPdfComponent', () => {
  let component: JobBreakdownPdfComponent;
  let fixture: ComponentFixture<JobBreakdownPdfComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JobBreakdownPdfComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JobBreakdownPdfComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
