import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { Status } from 'app/entities/enumerations/status.model';
import { IProjet, Projet } from '../projet.model';

import { ProjetService } from './projet.service';

describe('Projet Service', () => {
  let service: ProjetService;
  let httpMock: HttpTestingController;
  let elemDefault: IProjet;
  let expectedResult: IProjet | IProjet[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProjetService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      userUuid: 'AAAAAAA',
      nomProjet: 'AAAAAAA',
      dateDebut: currentDate,
      dateFin: currentDate,
      technologies: 'AAAAAAA',
      statusProjet: Status.ATTENTE,
      nombreTotal: 0,
      nombreRestant: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dateDebut: currentDate.format(DATE_FORMAT),
          dateFin: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Projet', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          dateDebut: currentDate.format(DATE_FORMAT),
          dateFin: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateDebut: currentDate,
          dateFin: currentDate,
        },
        returnedFromService
      );

      service.create(new Projet()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Projet', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          userUuid: 'BBBBBB',
          nomProjet: 'BBBBBB',
          dateDebut: currentDate.format(DATE_FORMAT),
          dateFin: currentDate.format(DATE_FORMAT),
          technologies: 'BBBBBB',
          statusProjet: 'BBBBBB',
          nombreTotal: 1,
          nombreRestant: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateDebut: currentDate,
          dateFin: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Projet', () => {
      const patchObject = Object.assign(
        {
          userUuid: 'BBBBBB',
          nomProjet: 'BBBBBB',
          dateDebut: currentDate.format(DATE_FORMAT),
          dateFin: currentDate.format(DATE_FORMAT),
        },
        new Projet()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          dateDebut: currentDate,
          dateFin: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Projet', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          userUuid: 'BBBBBB',
          nomProjet: 'BBBBBB',
          dateDebut: currentDate.format(DATE_FORMAT),
          dateFin: currentDate.format(DATE_FORMAT),
          technologies: 'BBBBBB',
          statusProjet: 'BBBBBB',
          nombreTotal: 1,
          nombreRestant: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateDebut: currentDate,
          dateFin: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Projet', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addProjetToCollectionIfMissing', () => {
      it('should add a Projet to an empty array', () => {
        const projet: IProjet = { id: 123 };
        expectedResult = service.addProjetToCollectionIfMissing([], projet);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(projet);
      });

      it('should not add a Projet to an array that contains it', () => {
        const projet: IProjet = { id: 123 };
        const projetCollection: IProjet[] = [
          {
            ...projet,
          },
          { id: 456 },
        ];
        expectedResult = service.addProjetToCollectionIfMissing(projetCollection, projet);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Projet to an array that doesn't contain it", () => {
        const projet: IProjet = { id: 123 };
        const projetCollection: IProjet[] = [{ id: 456 }];
        expectedResult = service.addProjetToCollectionIfMissing(projetCollection, projet);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(projet);
      });

      it('should add only unique Projet to an array', () => {
        const projetArray: IProjet[] = [{ id: 123 }, { id: 456 }, { id: 6867 }];
        const projetCollection: IProjet[] = [{ id: 123 }];
        expectedResult = service.addProjetToCollectionIfMissing(projetCollection, ...projetArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const projet: IProjet = { id: 123 };
        const projet2: IProjet = { id: 456 };
        expectedResult = service.addProjetToCollectionIfMissing([], projet, projet2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(projet);
        expect(expectedResult).toContain(projet2);
      });

      it('should accept null and undefined values', () => {
        const projet: IProjet = { id: 123 };
        expectedResult = service.addProjetToCollectionIfMissing([], null, projet, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(projet);
      });

      it('should return initial array if no Projet is added', () => {
        const projetCollection: IProjet[] = [{ id: 123 }];
        expectedResult = service.addProjetToCollectionIfMissing(projetCollection, undefined, null);
        expect(expectedResult).toEqual(projetCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
