import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DepartementUserComponent } from '../departement-user/departement-user.component';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
const departementUserRoute: Routes = [
  {
    path: '',
    component: DepartementUserComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(departementUserRoute)],
  exports: [RouterModule],
})
export class DepartementUserRoutingModule {}
