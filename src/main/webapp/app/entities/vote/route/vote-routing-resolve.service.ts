import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IVote, Vote } from '../vote.model';
import { VoteService } from '../service/vote.service';

@Injectable({ providedIn: 'root' })
export class VoteRoutingResolveService implements Resolve<IVote> {
  constructor(protected service: VoteService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVote> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((vote: HttpResponse<Vote>) => {
          if (vote.body) {
            return of(vote.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Vote());
  }
}
