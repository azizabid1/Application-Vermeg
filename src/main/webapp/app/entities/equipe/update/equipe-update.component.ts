import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IEquipe, Equipe } from '../equipe.model';
import { EquipeService } from '../service/equipe.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IVote } from 'app/entities/vote/vote.model';
import { VoteService } from 'app/entities/vote/service/vote.service';

@Component({
  selector: 'jhi-equipe-update',
  templateUrl: './equipe-update.component.html',
})
export class EquipeUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  votesSharedCollection: IVote[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [],
    nombrePersonne: [null, [Validators.min(4), Validators.max(6)]],
    users: [],
    vote: [],
  });

  constructor(
    protected equipeService: EquipeService,
    protected userService: UserService,
    protected voteService: VoteService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ equipe }) => {
      this.updateForm(equipe);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const equipe = this.createFromForm();
    if (equipe.id !== undefined) {
      this.subscribeToSaveResponse(this.equipeService.update(equipe));
    } else {
      this.subscribeToSaveResponse(this.equipeService.create(equipe));
    }
  }

  trackUserById(_index: number, item: IUser): number {
    return item.id!;
  }

  trackVoteById(_index: number, item: IVote): number {
    return item.id!;
  }

  getSelectedUser(option: IUser, selectedVals?: IUser[]): IUser {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEquipe>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(equipe: IEquipe): void {
    this.editForm.patchValue({
      id: equipe.id,
      nom: equipe.nom,
      nombrePersonne: equipe.nombrePersonne,
      users: equipe.users,
      vote: equipe.vote,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, ...(equipe.users ?? []));
    this.votesSharedCollection = this.voteService.addVoteToCollectionIfMissing(this.votesSharedCollection, equipe.vote);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, ...(this.editForm.get('users')!.value ?? []))))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.voteService
      .query()
      .pipe(map((res: HttpResponse<IVote[]>) => res.body ?? []))
      .pipe(map((votes: IVote[]) => this.voteService.addVoteToCollectionIfMissing(votes, this.editForm.get('vote')!.value)))
      .subscribe((votes: IVote[]) => (this.votesSharedCollection = votes));
  }

  protected createFromForm(): IEquipe {
    return {
      ...new Equipe(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      nombrePersonne: this.editForm.get(['nombrePersonne'])!.value,
      users: this.editForm.get(['users'])!.value,
      vote: this.editForm.get(['vote'])!.value,
    };
  }
}
