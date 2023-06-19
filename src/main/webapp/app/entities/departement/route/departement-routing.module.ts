import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DepartementComponent } from '../list/departement.component';
import { DepartementDetailComponent } from '../detail/departement-detail.component';
import { DepartementUpdateComponent } from '../update/departement-update.component';
import { DepartementRoutingResolveService } from './departement-routing-resolve.service';
import { UserManagementResolve } from 'app/admin/user-management/user-management.route';

const departementRoute: Routes = [
  {
    path: '',
    component: DepartementComponent,
    data: {
      defaultSort: 'id,asc',
      resolve: {
        user: UserManagementResolve,
      },
    },
    resolve: {
      user: UserManagementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DepartementDetailComponent,
    resolve: {
      departement: DepartementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DepartementUpdateComponent,
    resolve: {
      departement: DepartementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DepartementUpdateComponent,
    resolve: {
      departement: DepartementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(departementRoute)],
  exports: [RouterModule],
})
export class DepartementRoutingModule {}
