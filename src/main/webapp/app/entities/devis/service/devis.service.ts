import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDevis, getDevisIdentifier } from '../devis.model';

export type EntityResponseType = HttpResponse<IDevis>;
export type EntityArrayResponseType = HttpResponse<IDevis[]>;

@Injectable({ providedIn: 'root' })
export class DevisService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/devis');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(devis: IDevis): Observable<EntityResponseType> {
    return this.http.post<IDevis>(this.resourceUrl, devis, { observe: 'response' });
  }

  update(devis: IDevis): Observable<EntityResponseType> {
    return this.http.put<IDevis>(`${this.resourceUrl}/${getDevisIdentifier(devis) as number}`, devis, { observe: 'response' });
  }

  partialUpdate(devis: IDevis): Observable<EntityResponseType> {
    return this.http.patch<IDevis>(`${this.resourceUrl}/${getDevisIdentifier(devis) as number}`, devis, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDevis>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDevis[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addDevisToCollectionIfMissing(devisCollection: IDevis[], ...devisToCheck: (IDevis | null | undefined)[]): IDevis[] {
    const devis: IDevis[] = devisToCheck.filter(isPresent);
    if (devis.length > 0) {
      const devisCollectionIdentifiers = devisCollection.map(devisItem => getDevisIdentifier(devisItem)!);
      const devisToAdd = devis.filter(devisItem => {
        const devisIdentifier = getDevisIdentifier(devisItem);
        if (devisIdentifier == null || devisCollectionIdentifiers.includes(devisIdentifier)) {
          return false;
        }
        devisCollectionIdentifiers.push(devisIdentifier);
        return true;
      });
      return [...devisToAdd, ...devisCollection];
    }
    return devisCollection;
  }
}
