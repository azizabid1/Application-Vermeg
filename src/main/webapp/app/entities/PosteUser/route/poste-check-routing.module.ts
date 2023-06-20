import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import { PostecheckComponent } from '../postecheck/postecheck.component';

const posteCheckRoute: Routes = [
  {
    path: '',
    component: PostecheckComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(posteCheckRoute)],
  exports: [RouterModule],
})
export class PosteCheckRoutingModule {}
