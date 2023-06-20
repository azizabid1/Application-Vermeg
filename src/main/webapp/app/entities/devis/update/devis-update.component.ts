import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IDevis, Devis } from '../devis.model';
import { DevisService } from '../service/devis.service';
import { IProjet } from 'app/entities/projet/projet.model';
import { ProjetService } from 'app/entities/projet/service/projet.service';

@Component({
  selector: 'jhi-devis-update',
  templateUrl: './devis-update.component.html',
})
export class DevisUpdateComponent implements OnInit {
  isSaving = false;

  projetsCollection: IProjet[] = [];

  editForm = this.fb.group({
    id: [],
    prixTotal: [],
    prixHT: [],
    prixService: [],
    dureeProjet: [null, [Validators.min(0)]],
    projet: [],
  });

  constructor(
    protected devisService: DevisService,
    protected projetService: ProjetService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ devis }) => {
      this.updateForm(devis);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const devis = this.createFromForm();
    if (devis.id !== undefined) {
      this.subscribeToSaveResponse(this.devisService.update(devis));
    } else {
      this.subscribeToSaveResponse(this.devisService.create(devis));
    }
  }

  trackProjetById(_index: number, item: IProjet): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDevis>>): void {
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

  protected updateForm(devis: IDevis): void {
    this.editForm.patchValue({
      id: devis.id,
      prixTotal: devis.prixTotal,
      prixHT: devis.prixHT,
      prixService: devis.prixService,
      dureeProjet: devis.dureeProjet,
      projet: devis.projet,
    });

    this.projetsCollection = this.projetService.addProjetToCollectionIfMissing(this.projetsCollection, devis.projet);
  }

  protected loadRelationshipsOptions(): void {
    this.projetService
      .query({ 'devisId.specified': 'false' })
      .pipe(map((res: HttpResponse<IProjet[]>) => res.body ?? []))
      .pipe(map((projets: IProjet[]) => this.projetService.addProjetToCollectionIfMissing(projets, this.editForm.get('projet')!.value)))
      .subscribe((projets: IProjet[]) => (this.projetsCollection = projets));
  }

  protected createFromForm(): IDevis {
    return {
      ...new Devis(),
      id: this.editForm.get(['id'])!.value,
      prixTotal: this.editForm.get(['prixTotal'])!.value,
      prixHT: this.editForm.get(['prixHT'])!.value,
      prixService: this.editForm.get(['prixService'])!.value,
      dureeProjet: this.editForm.get(['dureeProjet'])!.value,
      projet: this.editForm.get(['projet'])!.value,
    };
  }
}
