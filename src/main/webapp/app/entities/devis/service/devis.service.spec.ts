import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IDevis, Devis } from '../devis.model';

import { DevisService } from './devis.service';

describe('Devis Service', () => {
  let service: DevisService;
  let httpMock: HttpTestingController;
  let elemDefault: IDevis;
  let expectedResult: IDevis | IDevis[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DevisService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      prixTotal: 0,
      prixHT: 0,
      prixService: 0,
      dureeProjet: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dureeProjet: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Devis', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          dureeProjet: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dureeProjet: currentDate,
        },
        returnedFromService
      );

      service.create(new Devis()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Devis', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          prixTotal: 1,
          prixHT: 1,
          prixService: 1,
          dureeProjet: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dureeProjet: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Devis', () => {
      const patchObject = Object.assign(
        {
          prixHT: 1,
          prixService: 1,
        },
        new Devis()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          dureeProjet: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Devis', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          prixTotal: 1,
          prixHT: 1,
          prixService: 1,
          dureeProjet: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dureeProjet: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Devis', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addDevisToCollectionIfMissing', () => {
      it('should add a Devis to an empty array', () => {
        const devis: IDevis = { id: 123 };
        expectedResult = service.addDevisToCollectionIfMissing([], devis);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(devis);
      });

      it('should not add a Devis to an array that contains it', () => {
        const devis: IDevis = { id: 123 };
        const devisCollection: IDevis[] = [
          {
            ...devis,
          },
          { id: 456 },
        ];
        expectedResult = service.addDevisToCollectionIfMissing(devisCollection, devis);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Devis to an array that doesn't contain it", () => {
        const devis: IDevis = { id: 123 };
        const devisCollection: IDevis[] = [{ id: 456 }];
        expectedResult = service.addDevisToCollectionIfMissing(devisCollection, devis);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(devis);
      });

      it('should add only unique Devis to an array', () => {
        const devisArray: IDevis[] = [{ id: 123 }, { id: 456 }, { id: 67561 }];
        const devisCollection: IDevis[] = [{ id: 123 }];
        expectedResult = service.addDevisToCollectionIfMissing(devisCollection, ...devisArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const devis: IDevis = { id: 123 };
        const devis2: IDevis = { id: 456 };
        expectedResult = service.addDevisToCollectionIfMissing([], devis, devis2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(devis);
        expect(expectedResult).toContain(devis2);
      });

      it('should accept null and undefined values', () => {
        const devis: IDevis = { id: 123 };
        expectedResult = service.addDevisToCollectionIfMissing([], null, devis, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(devis);
      });

      it('should return initial array if no Devis is added', () => {
        const devisCollection: IDevis[] = [{ id: 123 }];
        expectedResult = service.addDevisToCollectionIfMissing(devisCollection, undefined, null);
        expect(expectedResult).toEqual(devisCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
