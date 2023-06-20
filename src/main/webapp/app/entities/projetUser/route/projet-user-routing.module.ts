import { RouterModule, Routes } from '@angular/router';
import { ProjetUserComponent } from '../projet-user/projet-user.component';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NgModule } from '@angular/core';

const projetUserRoute: Routes = [
  {
    path: '',
    component: ProjetUserComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(projetUserRoute)],
  exports: [RouterModule],
})
export class ProjetUserRoutingModule {}
