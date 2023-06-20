import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EquipeUserComponent } from '../equipe-user.component';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

const equipeUserRoute: Routes = [
  {
    path: '',
    component: EquipeUserComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(equipeUserRoute)],
  exports: [RouterModule],
})
export class EquipeUserRoutingModule {}
