import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IVote, Vote } from '../vote.model';
import { VoteService } from '../service/vote.service';
import { Rendement } from 'app/entities/enumerations/rendement.model';

@Component({
  selector: 'jhi-vote-update',
  templateUrl: './vote-update.component.html',
})
export class VoteUpdateComponent implements OnInit {
  isSaving = false;
  rendementValues = Object.keys(Rendement);

  editForm = this.fb.group({
    id: [],
    userUuid: [null, [Validators.required]],
    typeVote: [null, [Validators.required]],
  });

  constructor(protected voteService: VoteService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ vote }) => {
      this.updateForm(vote);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const vote = this.createFromForm();
    if (vote.id !== undefined) {
      this.subscribeToSaveResponse(this.voteService.update(vote));
    } else {
      this.subscribeToSaveResponse(this.voteService.create(vote));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVote>>): void {
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

  protected updateForm(vote: IVote): void {
    this.editForm.patchValue({
      id: vote.id,
      userUuid: vote.userUuid,
      typeVote: vote.typeVote,
    });
  }

  protected createFromForm(): IVote {
    return {
      ...new Vote(),
      id: this.editForm.get(['id'])!.value,
      userUuid: this.editForm.get(['userUuid'])!.value,
      typeVote: this.editForm.get(['typeVote'])!.value,
    };
  }
}
