import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IStatusEmploye, getStatusEmployeIdentifier } from '../status-employe.model';

export type EntityResponseType = HttpResponse<IStatusEmploye>;
export type EntityArrayResponseType = HttpResponse<IStatusEmploye[]>;

@Injectable({ providedIn: 'root' })
export class StatusEmployeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/status-employes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(statusEmploye: IStatusEmploye): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(statusEmploye);
    return this.http
      .post<IStatusEmploye>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(statusEmploye: IStatusEmploye): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(statusEmploye);
    return this.http
      .put<IStatusEmploye>(`${this.resourceUrl}/${getStatusEmployeIdentifier(statusEmploye) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(statusEmploye: IStatusEmploye): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(statusEmploye);
    return this.http
      .patch<IStatusEmploye>(`${this.resourceUrl}/${getStatusEmployeIdentifier(statusEmploye) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IStatusEmploye>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IStatusEmploye[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addStatusEmployeToCollectionIfMissing(
    statusEmployeCollection: IStatusEmploye[],
    ...statusEmployesToCheck: (IStatusEmploye | null | undefined)[]
  ): IStatusEmploye[] {
    const statusEmployes: IStatusEmploye[] = statusEmployesToCheck.filter(isPresent);
    if (statusEmployes.length > 0) {
      const statusEmployeCollectionIdentifiers = statusEmployeCollection.map(
        statusEmployeItem => getStatusEmployeIdentifier(statusEmployeItem)!
      );
      const statusEmployesToAdd = statusEmployes.filter(statusEmployeItem => {
        const statusEmployeIdentifier = getStatusEmployeIdentifier(statusEmployeItem);
        if (statusEmployeIdentifier == null || statusEmployeCollectionIdentifiers.includes(statusEmployeIdentifier)) {
          return false;
        }
        statusEmployeCollectionIdentifiers.push(statusEmployeIdentifier);
        return true;
      });
      return [...statusEmployesToAdd, ...statusEmployeCollection];
    }
    return statusEmployeCollection;
  }

  protected convertDateFromClient(statusEmploye: IStatusEmploye): IStatusEmploye {
    return Object.assign({}, statusEmploye, {
      debutConge: statusEmploye.debutConge?.isValid() ? statusEmploye.debutConge.format(DATE_FORMAT) : undefined,
      finConge: statusEmploye.finConge?.isValid() ? statusEmploye.finConge.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.debutConge = res.body.debutConge ? dayjs(res.body.debutConge) : undefined;
      res.body.finConge = res.body.finConge ? dayjs(res.body.finConge) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((statusEmploye: IStatusEmploye) => {
        statusEmploye.debutConge = statusEmploye.debutConge ? dayjs(statusEmploye.debutConge) : undefined;
        statusEmploye.finConge = statusEmploye.finConge ? dayjs(statusEmploye.finConge) : undefined;
      });
    }
    return res;
  }
}
