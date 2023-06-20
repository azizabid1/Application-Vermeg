import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PosteCheckRoutingModule } from './route/poste-check-routing.module';

@NgModule({
  imports: [SharedModule, PosteCheckRoutingModule],
  declarations: [],
  entryComponents: [],
})
export class PosteCheckModule {}
