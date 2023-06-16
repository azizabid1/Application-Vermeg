import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { StatusEmployeComponent } from './list/status-employe.component';
import { StatusEmployeDetailComponent } from './detail/status-employe-detail.component';
import { StatusEmployeUpdateComponent } from './update/status-employe-update.component';
import { StatusEmployeDeleteDialogComponent } from './delete/status-employe-delete-dialog.component';
import { StatusEmployeRoutingModule } from './route/status-employe-routing.module';

@NgModule({
  imports: [SharedModule, StatusEmployeRoutingModule],
  declarations: [StatusEmployeComponent, StatusEmployeDetailComponent, StatusEmployeUpdateComponent, StatusEmployeDeleteDialogComponent],
  entryComponents: [StatusEmployeDeleteDialogComponent],
})
export class StatusEmployeModule {}
