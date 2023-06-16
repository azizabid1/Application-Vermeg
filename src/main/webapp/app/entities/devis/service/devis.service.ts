import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
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
    const copy = this.convertDateFromClient(devis);
    return this.http
      .post<IDevis>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(devis: IDevis): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(devis);
    return this.http
      .put<IDevis>(`${this.resourceUrl}/${getDevisIdentifier(devis) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(devis: IDevis): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(devis);
    return this.http
      .patch<IDevis>(`${this.resourceUrl}/${getDevisIdentifier(devis) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IDevis>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IDevis[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
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

  protected convertDateFromClient(devis: IDevis): IDevis {
    return Object.assign({}, devis, {
      dureeProjet: devis.dureeProjet?.isValid() ? devis.dureeProjet.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dureeProjet = res.body.dureeProjet ? dayjs(res.body.dureeProjet) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((devis: IDevis) => {
        devis.dureeProjet = devis.dureeProjet ? dayjs(devis.dureeProjet) : undefined;
      });
    }
    return res;
  }
}
