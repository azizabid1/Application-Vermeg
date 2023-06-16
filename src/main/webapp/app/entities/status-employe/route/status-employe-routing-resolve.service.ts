import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStatusEmploye, StatusEmploye } from '../status-employe.model';
import { StatusEmployeService } from '../service/status-employe.service';

@Injectable({ providedIn: 'root' })
export class StatusEmployeRoutingResolveService implements Resolve<IStatusEmploye> {
  constructor(protected service: StatusEmployeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IStatusEmploye> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((statusEmploye: HttpResponse<StatusEmploye>) => {
          if (statusEmploye.body) {
            return of(statusEmploye.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new StatusEmploye());
  }
}
