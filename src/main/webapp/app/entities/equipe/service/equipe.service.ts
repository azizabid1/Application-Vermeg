import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEquipe, getEquipeIdentifier } from '../equipe.model';

export type EntityResponseType = HttpResponse<IEquipe>;
export type EntityArrayResponseType = HttpResponse<IEquipe[]>;

@Injectable({ providedIn: 'root' })
export class EquipeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/equipes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(equipe: IEquipe): Observable<EntityResponseType> {
    return this.http.post<IEquipe>(this.resourceUrl, equipe, { observe: 'response' });
  }

  update(equipe: IEquipe): Observable<EntityResponseType> {
    return this.http.put<IEquipe>(`${this.resourceUrl}/${getEquipeIdentifier(equipe) as number}`, equipe, { observe: 'response' });
  }

  partialUpdate(equipe: IEquipe): Observable<EntityResponseType> {
    return this.http.patch<IEquipe>(`${this.resourceUrl}/${getEquipeIdentifier(equipe) as number}`, equipe, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEquipe>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEquipe[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEquipeToCollectionIfMissing(equipeCollection: IEquipe[], ...equipesToCheck: (IEquipe | null | undefined)[]): IEquipe[] {
    const equipes: IEquipe[] = equipesToCheck.filter(isPresent);
    if (equipes.length > 0) {
      const equipeCollectionIdentifiers = equipeCollection.map(equipeItem => getEquipeIdentifier(equipeItem)!);
      const equipesToAdd = equipes.filter(equipeItem => {
        const equipeIdentifier = getEquipeIdentifier(equipeItem);
        if (equipeIdentifier == null || equipeCollectionIdentifiers.includes(equipeIdentifier)) {
          return false;
        }
        equipeCollectionIdentifiers.push(equipeIdentifier);
        return true;
      });
      return [...equipesToAdd, ...equipeCollection];
    }
    return equipeCollection;
  }
}
