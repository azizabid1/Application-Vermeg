import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EquipeUserComponent } from './equipe-user.component';

describe('EquipeUserComponent', () => {
  let component: EquipeUserComponent;
  let fixture: ComponentFixture<EquipeUserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EquipeUserComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EquipeUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
