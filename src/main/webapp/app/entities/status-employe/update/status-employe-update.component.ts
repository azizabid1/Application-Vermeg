import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IStatusEmploye, StatusEmploye } from '../status-employe.model';
import { StatusEmployeService } from '../service/status-employe.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-status-employe-update',
  templateUrl: './status-employe-update.component.html',
})
export class StatusEmployeUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    disponibilite: [],
    mission: [],
    debutConge: [],
    finConge: [],
    userId: [],
  });

  constructor(
    protected statusEmployeService: StatusEmployeService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ statusEmploye }) => {
      this.updateForm(statusEmploye);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const statusEmploye = this.createFromForm();
    if (statusEmploye.id !== undefined) {
      this.subscribeToSaveResponse(this.statusEmployeService.update(statusEmploye));
    } else {
      this.subscribeToSaveResponse(this.statusEmployeService.create(statusEmploye));
    }
  }

  trackUserById(_index: number, item: IUser): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStatusEmploye>>): void {
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

  protected updateForm(statusEmploye: IStatusEmploye): void {
    this.editForm.patchValue({
      id: statusEmploye.id,
      disponibilite: statusEmploye.disponibilite,
      mission: statusEmploye.mission,
      debutConge: statusEmploye.debutConge,
      finConge: statusEmploye.finConge,
      userId: statusEmploye.userId,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, statusEmploye.userId);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('userId')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): IStatusEmploye {
    return {
      ...new StatusEmploye(),
      id: this.editForm.get(['id'])!.value,
      disponibilite: this.editForm.get(['disponibilite'])!.value,
      mission: this.editForm.get(['mission'])!.value,
      debutConge: this.editForm.get(['debutConge'])!.value,
      finConge: this.editForm.get(['finConge'])!.value,
      userId: this.editForm.get(['userId'])!.value,
    };
  }
}
