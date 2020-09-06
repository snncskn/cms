import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PartRequestsComponent } from './part-requests.component';

describe('PartRequestsComponent', () => {
  let component: PartRequestsComponent;
  let fixture: ComponentFixture<PartRequestsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PartRequestsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PartRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
