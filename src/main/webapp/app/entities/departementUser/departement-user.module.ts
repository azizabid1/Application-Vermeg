import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DepartementUserRoutingModule } from './route/departement-user-routing.module';
import { DepartementUserComponent } from './departement-user/departement-user.component';

@NgModule({
  imports: [SharedModule, DepartementUserRoutingModule],
  declarations: [DepartementUserComponent],
  entryComponents: [],
})
export class DepartementUserModule {}
