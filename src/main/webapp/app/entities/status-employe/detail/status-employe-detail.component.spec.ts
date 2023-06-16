import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { StatusEmployeDetailComponent } from './status-employe-detail.component';

describe('StatusEmploye Management Detail Component', () => {
  let comp: StatusEmployeDetailComponent;
  let fixture: ComponentFixture<StatusEmployeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StatusEmployeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ statusEmploye: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(StatusEmployeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(StatusEmployeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load statusEmploye on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.statusEmploye).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
