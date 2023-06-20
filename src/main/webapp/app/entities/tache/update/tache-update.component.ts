import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITache, Tache } from '../tache.model';
import { TacheService } from '../service/tache.service';
import { Status } from 'app/entities/enumerations/status.model';

@Component({
  selector: 'jhi-tache-update',
  templateUrl: './tache-update.component.html',
})
export class TacheUpdateComponent implements OnInit {
  isSaving = false;
  statusValues = Object.keys(Status);

  editForm = this.fb.group({
    id: [],
    titre: [],
    description: [],
    statusTache: [],
  });

  constructor(protected tacheService: TacheService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tache }) => {
      this.updateForm(tache);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tache = this.createFromForm();
    if (tache.id !== undefined) {
      this.subscribeToSaveResponse(this.tacheService.update(tache));
    } else {
      this.subscribeToSaveResponse(this.tacheService.create(tache));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITache>>): void {
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

  protected updateForm(tache: ITache): void {
    this.editForm.patchValue({
      id: tache.id,
      titre: tache.titre,
      description: tache.description,
      statusTache: tache.statusTache,
    });
  }

  protected createFromForm(): ITache {
    return {
      ...new Tache(),
      id: this.editForm.get(['id'])!.value,
      titre: this.editForm.get(['titre'])!.value,
      description: this.editForm.get(['description'])!.value,
      statusTache: this.editForm.get(['statusTache'])!.value,
    };
  }
}
