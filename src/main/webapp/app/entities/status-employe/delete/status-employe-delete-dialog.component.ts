import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IStatusEmploye } from '../status-employe.model';
import { StatusEmployeService } from '../service/status-employe.service';

@Component({
  templateUrl: './status-employe-delete-dialog.component.html',
})
export class StatusEmployeDeleteDialogComponent {
  statusEmploye?: IStatusEmploye;

  constructor(protected statusEmployeService: StatusEmployeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.statusEmployeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
