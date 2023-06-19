import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DevisService } from '../service/devis.service';
import { IDevis, Devis } from '../devis.model';
import { IProjet } from 'app/entities/projet/projet.model';
import { ProjetService } from 'app/entities/projet/service/projet.service';

import { DevisUpdateComponent } from './devis-update.component';

describe('Devis Management Update Component', () => {
  let comp: DevisUpdateComponent;
  let fixture: ComponentFixture<DevisUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let devisService: DevisService;
  let projetService: ProjetService;

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
    projetService = TestBed.inject(ProjetService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call projet query and add missing value', () => {
      const devis: IDevis = { id: 456 };
      const projet: IProjet = { id: 41050 };
      devis.projet = projet;

      const projetCollection: IProjet[] = [{ id: 84471 }];
      jest.spyOn(projetService, 'query').mockReturnValue(of(new HttpResponse({ body: projetCollection })));
      const expectedCollection: IProjet[] = [projet, ...projetCollection];
      jest.spyOn(projetService, 'addProjetToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ devis });
      comp.ngOnInit();

      expect(projetService.query).toHaveBeenCalled();
      expect(projetService.addProjetToCollectionIfMissing).toHaveBeenCalledWith(projetCollection, projet);
      expect(comp.projetsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const devis: IDevis = { id: 456 };
      const projet: IProjet = { id: 31999 };
      devis.projet = projet;

      activatedRoute.data = of({ devis });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(devis));
      expect(comp.projetsCollection).toContain(projet);
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

  describe('Tracking relationships identifiers', () => {
    describe('trackProjetById', () => {
      it('Should return tracked Projet primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackProjetById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
