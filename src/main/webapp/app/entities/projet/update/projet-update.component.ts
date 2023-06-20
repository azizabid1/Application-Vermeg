import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IProjet, Projet } from '../projet.model';
import { ProjetService } from '../service/projet.service';
import { IEquipe } from 'app/entities/equipe/equipe.model';
import { EquipeService } from 'app/entities/equipe/service/equipe.service';
import { ITache } from 'app/entities/tache/tache.model';
import { TacheService } from 'app/entities/tache/service/tache.service';
import { Status } from 'app/entities/enumerations/status.model';

@Component({
  selector: 'jhi-projet-update',
  templateUrl: './projet-update.component.html',
})
export class ProjetUpdateComponent implements OnInit {
  isSaving = false;
  statusValues = Object.keys(Status);

  equipesCollection: IEquipe[] = [];
  tachesSharedCollection: ITache[] = [];

  editForm = this.fb.group({
    id: [],
    nomProjet: [],
    dateDebut: [],
    dateFin: [],
    technologies: [],
    statusProjet: [],
    nombreTotal: [],
    nombreRestant: [],
    equipe: [],
    taches: [],
  });

  constructor(
    protected projetService: ProjetService,
    protected equipeService: EquipeService,
    protected tacheService: TacheService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ projet }) => {
      this.updateForm(projet);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const projet = this.createFromForm();
    if (projet.id !== undefined) {
      this.subscribeToSaveResponse(this.projetService.update(projet));
    } else {
      this.subscribeToSaveResponse(this.projetService.create(projet));
    }
  }

  trackEquipeById(_index: number, item: IEquipe): number {
    return item.id!;
  }

  trackTacheById(_index: number, item: ITache): number {
    return item.id!;
  }

  getSelectedTache(option: ITache, selectedVals?: ITache[]): ITache {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProjet>>): void {
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

  protected updateForm(projet: IProjet): void {
    this.editForm.patchValue({
      id: projet.id,
      nomProjet: projet.nomProjet,
      dateDebut: projet.dateDebut,
      dateFin: projet.dateFin,
      technologies: projet.technologies,
      statusProjet: projet.statusProjet,
      nombreTotal: projet.nombreTotal,
      nombreRestant: projet.nombreRestant,
      equipe: projet.equipe,
      taches: projet.taches,
    });

    this.equipesCollection = this.equipeService.addEquipeToCollectionIfMissing(this.equipesCollection, projet.equipe);
    this.tachesSharedCollection = this.tacheService.addTacheToCollectionIfMissing(this.tachesSharedCollection, ...(projet.taches ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.equipeService
      .query({ 'projetId.specified': 'false' })
      .pipe(map((res: HttpResponse<IEquipe[]>) => res.body ?? []))
      .pipe(map((equipes: IEquipe[]) => this.equipeService.addEquipeToCollectionIfMissing(equipes, this.editForm.get('equipe')!.value)))
      .subscribe((equipes: IEquipe[]) => (this.equipesCollection = equipes));

    this.tacheService
      .query()
      .pipe(map((res: HttpResponse<ITache[]>) => res.body ?? []))
      .pipe(
        map((taches: ITache[]) => this.tacheService.addTacheToCollectionIfMissing(taches, ...(this.editForm.get('taches')!.value ?? [])))
      )
      .subscribe((taches: ITache[]) => (this.tachesSharedCollection = taches));
  }

  protected createFromForm(): IProjet {
    return {
      ...new Projet(),
      id: this.editForm.get(['id'])!.value,
      nomProjet: this.editForm.get(['nomProjet'])!.value,
      dateDebut: this.editForm.get(['dateDebut'])!.value,
      dateFin: this.editForm.get(['dateFin'])!.value,
      technologies: this.editForm.get(['technologies'])!.value,
      statusProjet: this.editForm.get(['statusProjet'])!.value,
      nombreTotal: this.editForm.get(['nombreTotal'])!.value,
      nombreRestant: this.editForm.get(['nombreRestant'])!.value,
      equipe: this.editForm.get(['equipe'])!.value,
      taches: this.editForm.get(['taches'])!.value,
    };
  }
}
