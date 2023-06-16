import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TacheService } from '../service/tache.service';
import { ITache, Tache } from '../tache.model';

import { TacheUpdateComponent } from './tache-update.component';

describe('Tache Management Update Component', () => {
  let comp: TacheUpdateComponent;
  let fixture: ComponentFixture<TacheUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tacheService: TacheService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TacheUpdateComponent],
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
      .overrideTemplate(TacheUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TacheUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tacheService = TestBed.inject(TacheService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const tache: ITache = { id: 456 };

      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(tache));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tache>>();
      const tache = { id: 123 };
      jest.spyOn(tacheService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tache }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(tacheService.update).toHaveBeenCalledWith(tache);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tache>>();
      const tache = new Tache();
      jest.spyOn(tacheService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tache }));
      saveSubject.complete();

      // THEN
      expect(tacheService.create).toHaveBeenCalledWith(tache);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tache>>();
      const tache = { id: 123 };
      jest.spyOn(tacheService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tacheService.update).toHaveBeenCalledWith(tache);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
