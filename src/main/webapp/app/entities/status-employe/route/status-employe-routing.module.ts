import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { StatusEmployeComponent } from '../list/status-employe.component';
import { StatusEmployeDetailComponent } from '../detail/status-employe-detail.component';
import { StatusEmployeUpdateComponent } from '../update/status-employe-update.component';
import { StatusEmployeRoutingResolveService } from './status-employe-routing-resolve.service';

const statusEmployeRoute: Routes = [
  {
    path: '',
    component: StatusEmployeComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: StatusEmployeDetailComponent,
    resolve: {
      statusEmploye: StatusEmployeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: StatusEmployeUpdateComponent,
    resolve: {
      statusEmploye: StatusEmployeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: StatusEmployeUpdateComponent,
    resolve: {
      statusEmploye: StatusEmployeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(statusEmployeRoute)],
  exports: [RouterModule],
})
export class StatusEmployeRoutingModule {}
