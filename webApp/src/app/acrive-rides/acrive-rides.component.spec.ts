import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AcriveRidesComponent } from './acrive-rides.component';

describe('AcriveRidesComponent', () => {
  let component: AcriveRidesComponent;
  let fixture: ComponentFixture<AcriveRidesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AcriveRidesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AcriveRidesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
