import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DevisService } from '../service/devis.service';
import { IDevis, Devis } from '../devis.model';

import { DevisUpdateComponent } from './devis-update.component';

describe('Devis Management Update Component', () => {
  let comp: DevisUpdateComponent;
  let fixture: ComponentFixture<DevisUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let devisService: DevisService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DevisUpdateComponent],
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
      .overrideTemplate(DevisUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DevisUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    devisService = TestBed.inject(DevisService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const devis: IDevis = { id: 456 };

      activatedRoute.data = of({ devis });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(devis));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Devis>>();
      const devis = { id: 123 };
      jest.spyOn(devisService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ devis });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: devis }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(devisService.update).toHaveBeenCalledWith(devis);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Devis>>();
      const devis = new Devis();
      jest.spyOn(devisService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ devis });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: devis }));
      saveSubject.complete();

      // THEN
      expect(devisService.create).toHaveBeenCalledWith(devis);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Devis>>();
      const devis = { id: 123 };
      jest.spyOn(devisService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ devis });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(devisService.update).toHaveBeenCalledWith(devis);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
