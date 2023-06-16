import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDevis } from '../devis.model';
import { DevisService } from '../service/devis.service';

@Component({
  templateUrl: './devis-delete-dialog.component.html',
})
export class DevisDeleteDialogComponent {
  devis?: IDevis;

  constructor(protected devisService: DevisService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.devisService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
