import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PosteCheckRoutingModule } from './route/poste-check-routing.module';
import { PostecheckComponent } from './postecheck/postecheck.component';

@NgModule({
  imports: [SharedModule, PosteCheckRoutingModule],
  declarations: [PostecheckComponent],
  entryComponents: [],
})
export class PosteCheckModule {}
