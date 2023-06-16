import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IVote } from '../vote.model';
import { VoteService } from '../service/vote.service';

@Component({
  templateUrl: './vote-delete-dialog.component.html',
})
export class VoteDeleteDialogComponent {
  vote?: IVote;

  constructor(protected voteService: VoteService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.voteService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
