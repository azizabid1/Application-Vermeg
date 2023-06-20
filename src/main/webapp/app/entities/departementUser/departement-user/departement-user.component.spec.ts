import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DepartementUserComponent } from './departement-user.component';

describe('DepartementUserComponent', () => {
  let component: DepartementUserComponent;
  let fixture: ComponentFixture<DepartementUserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DepartementUserComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DepartementUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
