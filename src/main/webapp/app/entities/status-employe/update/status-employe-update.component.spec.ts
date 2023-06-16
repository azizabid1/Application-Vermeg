import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { StatusEmployeService } from '../service/status-employe.service';
import { IStatusEmploye, StatusEmploye } from '../status-employe.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { StatusEmployeUpdateComponent } from './status-employe-update.component';

describe('StatusEmploye Management Update Component', () => {
  let comp: StatusEmployeUpdateComponent;
  let fixture: ComponentFixture<StatusEmployeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let statusEmployeService: StatusEmployeService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [StatusEmployeUpdateComponent],
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
      .overrideTemplate(StatusEmployeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StatusEmployeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    statusEmployeService = TestBed.inject(StatusEmployeService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const statusEmploye: IStatusEmploye = { id: 456 };
      const userId: IUser = { id: 45941 };
      statusEmploye.userId = userId;

      const userCollection: IUser[] = [{ id: 14999 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [userId];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ statusEmploye });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const statusEmploye: IStatusEmploye = { id: 456 };
      const userId: IUser = { id: 56497 };
      statusEmploye.userId = userId;

      activatedRoute.data = of({ statusEmploye });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(statusEmploye));
      expect(comp.usersSharedCollection).toContain(userId);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<StatusEmploye>>();
      const statusEmploye = { id: 123 };
      jest.spyOn(statusEmployeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ statusEmploye });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: statusEmploye }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(statusEmployeService.update).toHaveBeenCalledWith(statusEmploye);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<StatusEmploye>>();
      const statusEmploye = new StatusEmploye();
      jest.spyOn(statusEmployeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ statusEmploye });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: statusEmploye }));
      saveSubject.complete();

      // THEN
      expect(statusEmployeService.create).toHaveBeenCalledWith(statusEmploye);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<StatusEmploye>>();
      const statusEmploye = { id: 123 };
      jest.spyOn(statusEmployeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ statusEmploye });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(statusEmployeService.update).toHaveBeenCalledWith(statusEmploye);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUserById', () => {
      it('Should return tracked User primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
