import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProjetService } from '../service/projet.service';
import { IProjet, Projet } from '../projet.model';
import { IDevis } from 'app/entities/devis/devis.model';
import { DevisService } from 'app/entities/devis/service/devis.service';
import { IEquipe } from 'app/entities/equipe/equipe.model';
import { EquipeService } from 'app/entities/equipe/service/equipe.service';
import { ITache } from 'app/entities/tache/tache.model';
import { TacheService } from 'app/entities/tache/service/tache.service';

import { ProjetUpdateComponent } from './projet-update.component';

describe('Projet Management Update Component', () => {
  let comp: ProjetUpdateComponent;
  let fixture: ComponentFixture<ProjetUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let projetService: ProjetService;
  let devisService: DevisService;
  let equipeService: EquipeService;
  let tacheService: TacheService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProjetUpdateComponent],
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
      .overrideTemplate(ProjetUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProjetUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    projetService = TestBed.inject(ProjetService);
    devisService = TestBed.inject(DevisService);
    equipeService = TestBed.inject(EquipeService);
    tacheService = TestBed.inject(TacheService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call devis query and add missing value', () => {
      const projet: IProjet = { id: 456 };
      const devis: IDevis = { id: 4730 };
      projet.devis = devis;

      const devisCollection: IDevis[] = [{ id: 70588 }];
      jest.spyOn(devisService, 'query').mockReturnValue(of(new HttpResponse({ body: devisCollection })));
      const expectedCollection: IDevis[] = [devis, ...devisCollection];
      jest.spyOn(devisService, 'addDevisToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ projet });
      comp.ngOnInit();

      expect(devisService.query).toHaveBeenCalled();
      expect(devisService.addDevisToCollectionIfMissing).toHaveBeenCalledWith(devisCollection, devis);
      expect(comp.devisCollection).toEqual(expectedCollection);
    });

    it('Should call equipe query and add missing value', () => {
      const projet: IProjet = { id: 456 };
      const equipe: IEquipe = { id: 30685 };
      projet.equipe = equipe;

      const equipeCollection: IEquipe[] = [{ id: 81302 }];
      jest.spyOn(equipeService, 'query').mockReturnValue(of(new HttpResponse({ body: equipeCollection })));
      const expectedCollection: IEquipe[] = [equipe, ...equipeCollection];
      jest.spyOn(equipeService, 'addEquipeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ projet });
      comp.ngOnInit();

      expect(equipeService.query).toHaveBeenCalled();
      expect(equipeService.addEquipeToCollectionIfMissing).toHaveBeenCalledWith(equipeCollection, equipe);
      expect(comp.equipesCollection).toEqual(expectedCollection);
    });

    it('Should call Tache query and add missing value', () => {
      const projet: IProjet = { id: 456 };
      const tache: ITache = { id: 48323 };
      projet.tache = tache;

      const tacheCollection: ITache[] = [{ id: 21370 }];
      jest.spyOn(tacheService, 'query').mockReturnValue(of(new HttpResponse({ body: tacheCollection })));
      const additionalTaches = [tache];
      const expectedCollection: ITache[] = [...additionalTaches, ...tacheCollection];
      jest.spyOn(tacheService, 'addTacheToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ projet });
      comp.ngOnInit();

      expect(tacheService.query).toHaveBeenCalled();
      expect(tacheService.addTacheToCollectionIfMissing).toHaveBeenCalledWith(tacheCollection, ...additionalTaches);
      expect(comp.tachesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const projet: IProjet = { id: 456 };
      const devis: IDevis = { id: 11116 };
      projet.devis = devis;
      const equipe: IEquipe = { id: 14209 };
      projet.equipe = equipe;
      const tache: ITache = { id: 19396 };
      projet.tache = tache;

      activatedRoute.data = of({ projet });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(projet));
      expect(comp.devisCollection).toContain(devis);
      expect(comp.equipesCollection).toContain(equipe);
      expect(comp.tachesSharedCollection).toContain(tache);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Projet>>();
      const projet = { id: 123 };
      jest.spyOn(projetService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: projet }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(projetService.update).toHaveBeenCalledWith(projet);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Projet>>();
      const projet = new Projet();
      jest.spyOn(projetService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: projet }));
      saveSubject.complete();

      // THEN
      expect(projetService.create).toHaveBeenCalledWith(projet);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Projet>>();
      const projet = { id: 123 };
      jest.spyOn(projetService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ projet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(projetService.update).toHaveBeenCalledWith(projet);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackDevisById', () => {
      it('Should return tracked Devis primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackDevisById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackEquipeById', () => {
      it('Should return tracked Equipe primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackEquipeById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackTacheById', () => {
      it('Should return tracked Tache primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTacheById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
