import { NgModule } from '@angular/core';
import { EquipeUserRoutingModule } from './equipe-user/route/equipe-user-routing.module';
import { SharedModule } from 'app/shared/shared.module';
import { EquipeUserComponent } from './equipe-user/equipe-user.component';

@NgModule({
  imports: [SharedModule, EquipeUserRoutingModule],
  declarations: [EquipeUserComponent],
  entryComponents: [],
})
export class EquipeUserModule {}
