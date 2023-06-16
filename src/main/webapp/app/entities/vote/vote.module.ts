import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { VoteComponent } from './list/vote.component';
import { VoteDetailComponent } from './detail/vote-detail.component';
import { VoteUpdateComponent } from './update/vote-update.component';
import { VoteDeleteDialogComponent } from './delete/vote-delete-dialog.component';
import { VoteRoutingModule } from './route/vote-routing.module';

@NgModule({
  imports: [SharedModule, VoteRoutingModule],
  declarations: [VoteComponent, VoteDetailComponent, VoteUpdateComponent, VoteDeleteDialogComponent],
  entryComponents: [VoteDeleteDialogComponent],
})
export class VoteModule {}
