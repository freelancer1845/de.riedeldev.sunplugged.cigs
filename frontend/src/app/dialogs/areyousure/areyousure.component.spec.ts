import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AreyousureComponent } from './areyousure.component';

describe('AreyousureComponent', () => {
  let component: AreyousureComponent;
  let fixture: ComponentFixture<AreyousureComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AreyousureComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AreyousureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
