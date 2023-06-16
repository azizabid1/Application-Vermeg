import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DevisComponent } from '../list/devis.component';
import { DevisDetailComponent } from '../detail/devis-detail.component';
import { DevisUpdateComponent } from '../update/devis-update.component';
import { DevisRoutingResolveService } from './devis-routing-resolve.service';

const devisRoute: Routes = [
  {
    path: '',
    component: DevisComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DevisDetailComponent,
    resolve: {
      devis: DevisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DevisUpdateComponent,
    resolve: {
      devis: DevisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DevisUpdateComponent,
    resolve: {
      devis: DevisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(devisRoute)],
  exports: [RouterModule],
})
export class DevisRoutingModule {}
