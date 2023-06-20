import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostecheckComponent } from './postecheck.component';

describe('PostecheckComponent', () => {
  let component: PostecheckComponent;
  let fixture: ComponentFixture<PostecheckComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PostecheckComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PostecheckComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
