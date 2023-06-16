import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IVote, getVoteIdentifier } from '../vote.model';

export type EntityResponseType = HttpResponse<IVote>;
export type EntityArrayResponseType = HttpResponse<IVote[]>;

@Injectable({ providedIn: 'root' })
export class VoteService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/votes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(vote: IVote): Observable<EntityResponseType> {
    return this.http.post<IVote>(this.resourceUrl, vote, { observe: 'response' });
  }

  update(vote: IVote): Observable<EntityResponseType> {
    return this.http.put<IVote>(`${this.resourceUrl}/${getVoteIdentifier(vote) as number}`, vote, { observe: 'response' });
  }

  partialUpdate(vote: IVote): Observable<EntityResponseType> {
    return this.http.patch<IVote>(`${this.resourceUrl}/${getVoteIdentifier(vote) as number}`, vote, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IVote>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IVote[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addVoteToCollectionIfMissing(voteCollection: IVote[], ...votesToCheck: (IVote | null | undefined)[]): IVote[] {
    const votes: IVote[] = votesToCheck.filter(isPresent);
    if (votes.length > 0) {
      const voteCollectionIdentifiers = voteCollection.map(voteItem => getVoteIdentifier(voteItem)!);
      const votesToAdd = votes.filter(voteItem => {
        const voteIdentifier = getVoteIdentifier(voteItem);
        if (voteIdentifier == null || voteCollectionIdentifiers.includes(voteIdentifier)) {
          return false;
        }
        voteCollectionIdentifiers.push(voteIdentifier);
        return true;
      });
      return [...votesToAdd, ...voteCollection];
    }
    return voteCollection;
  }
}
