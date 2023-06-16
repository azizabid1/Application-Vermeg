import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { VoteService } from '../service/vote.service';
import { IVote, Vote } from '../vote.model';

import { VoteUpdateComponent } from './vote-update.component';

describe('Vote Management Update Component', () => {
  let comp: VoteUpdateComponent;
  let fixture: ComponentFixture<VoteUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let voteService: VoteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [VoteUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(VoteUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VoteUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    voteService = TestBed.inject(VoteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const vote: IVote = { id: 456 };

      activatedRoute.data = of({ vote });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(vote));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Vote>>();
      const vote = { id: 123 };
      jest.spyOn(voteService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vote });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: vote }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(voteService.update).toHaveBeenCalledWith(vote);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Vote>>();
      const vote = new Vote();
      jest.spyOn(voteService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vote });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: vote }));
      saveSubject.complete();

      // THEN
      expect(voteService.create).toHaveBeenCalledWith(vote);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Vote>>();
      const vote = { id: 123 };
      jest.spyOn(voteService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vote });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(voteService.update).toHaveBeenCalledWith(vote);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
