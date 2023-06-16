import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDevis, Devis } from '../devis.model';
import { DevisService } from '../service/devis.service';

@Injectable({ providedIn: 'root' })
export class DevisRoutingResolveService implements Resolve<IDevis> {
  constructor(protected service: DevisService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDevis> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((devis: HttpResponse<Devis>) => {
          if (devis.body) {
            return of(devis.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Devis());
  }
}
