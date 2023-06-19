import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EquipeService } from '../service/equipe.service';
import { IEquipe, Equipe } from '../equipe.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IVote } from 'app/entities/vote/vote.model';
import { VoteService } from 'app/entities/vote/service/vote.service';

import { EquipeUpdateComponent } from './equipe-update.component';

describe('Equipe Management Update Component', () => {
  let comp: EquipeUpdateComponent;
  let fixture: ComponentFixture<EquipeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let equipeService: EquipeService;
  let userService: UserService;
  let voteService: VoteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EquipeUpdateComponent],
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
      .overrideTemplate(EquipeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EquipeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    equipeService = TestBed.inject(EquipeService);
    userService = TestBed.inject(UserService);
    voteService = TestBed.inject(VoteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const equipe: IEquipe = { id: 456 };
      const users: IUser[] = [{ id: 24897 }];
      equipe.users = users;

      const userCollection: IUser[] = [{ id: 29668 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [...users];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ equipe });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Vote query and add missing value', () => {
      const equipe: IEquipe = { id: 456 };
      const vote: IVote = { id: 43706 };
      equipe.vote = vote;

      const voteCollection: IVote[] = [{ id: 11580 }];
      jest.spyOn(voteService, 'query').mockReturnValue(of(new HttpResponse({ body: voteCollection })));
      const additionalVotes = [vote];
      const expectedCollection: IVote[] = [...additionalVotes, ...voteCollection];
      jest.spyOn(voteService, 'addVoteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ equipe });
      comp.ngOnInit();

      expect(voteService.query).toHaveBeenCalled();
      expect(voteService.addVoteToCollectionIfMissing).toHaveBeenCalledWith(voteCollection, ...additionalVotes);
      expect(comp.votesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const equipe: IEquipe = { id: 456 };
      const users: IUser = { id: 84858 };
      equipe.users = [users];
      const vote: IVote = { id: 31075 };
      equipe.vote = vote;

      activatedRoute.data = of({ equipe });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(equipe));
      expect(comp.usersSharedCollection).toContain(users);
      expect(comp.votesSharedCollection).toContain(vote);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Equipe>>();
      const equipe = { id: 123 };
      jest.spyOn(equipeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ equipe });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: equipe }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(equipeService.update).toHaveBeenCalledWith(equipe);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Equipe>>();
      const equipe = new Equipe();
      jest.spyOn(equipeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ equipe });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: equipe }));
      saveSubject.complete();

      // THEN
      expect(equipeService.create).toHaveBeenCalledWith(equipe);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Equipe>>();
      const equipe = { id: 123 };
      jest.spyOn(equipeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ equipe });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(equipeService.update).toHaveBeenCalledWith(equipe);
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

    describe('trackVoteById', () => {
      it('Should return tracked Vote primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackVoteById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedUser', () => {
      it('Should return option if no User is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedUser(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected User for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedUser(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this User is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedUser(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
