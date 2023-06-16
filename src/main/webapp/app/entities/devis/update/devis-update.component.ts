import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IDevis, Devis } from '../devis.model';
import { DevisService } from '../service/devis.service';

@Component({
  selector: 'jhi-devis-update',
  templateUrl: './devis-update.component.html',
})
export class DevisUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    prixTotal: [],
    prixHT: [],
    prixService: [],
    dureeProjet: [],
  });

  constructor(protected devisService: DevisService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ devis }) => {
      this.updateForm(devis);
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
    });
  }

  protected createFromForm(): IDevis {
    return {
      ...new Devis(),
      id: this.editForm.get(['id'])!.value,
      prixTotal: this.editForm.get(['prixTotal'])!.value,
      prixHT: this.editForm.get(['prixHT'])!.value,
      prixService: this.editForm.get(['prixService'])!.value,
      dureeProjet: this.editForm.get(['dureeProjet'])!.value,
    };
  }
}
