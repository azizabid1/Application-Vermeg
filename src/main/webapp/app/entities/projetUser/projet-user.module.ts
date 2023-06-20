import { SharedModule } from 'app/shared/shared.module';
import { ProjetUserComponent } from './projet-user/projet-user.component';
import { ProjetUserRoutingModule } from './route/projet-user-routing.module';
import { NgModule } from '@angular/core';

@NgModule({
  imports: [SharedModule, ProjetUserRoutingModule],
  declarations: [ProjetUserComponent],
  entryComponents: [],
})
export class ProjetUserModule {}
