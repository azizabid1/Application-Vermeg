import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { VoteDetailComponent } from './vote-detail.component';

describe('Vote Management Detail Component', () => {
  let comp: VoteDetailComponent;
  let fixture: ComponentFixture<VoteDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VoteDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ vote: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(VoteDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(VoteDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load vote on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.vote).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
