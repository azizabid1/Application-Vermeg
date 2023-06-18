import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IStatusEmploye, StatusEmploye } from '../status-employe.model';

import { StatusEmployeService } from './status-employe.service';

describe('StatusEmploye Service', () => {
  let service: StatusEmployeService;
  let httpMock: HttpTestingController;
  let elemDefault: IStatusEmploye;
  let expectedResult: IStatusEmploye | IStatusEmploye[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(StatusEmployeService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      userUuid: 'AAAAAAA',
      disponibilite: false,
      mission: false,
      debutConge: currentDate,
      finConge: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          debutConge: currentDate.format(DATE_FORMAT),
          finConge: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a StatusEmploye', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          debutConge: currentDate.format(DATE_FORMAT),
          finConge: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          debutConge: currentDate,
          finConge: currentDate,
        },
        returnedFromService
      );

      service.create(new StatusEmploye()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a StatusEmploye', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          userUuid: 'BBBBBB',
          disponibilite: true,
          mission: true,
          debutConge: currentDate.format(DATE_FORMAT),
          finConge: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          debutConge: currentDate,
          finConge: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a StatusEmploye', () => {
      const patchObject = Object.assign(
        {
          userUuid: 'BBBBBB',
        },
        new StatusEmploye()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          debutConge: currentDate,
          finConge: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of StatusEmploye', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          userUuid: 'BBBBBB',
          disponibilite: true,
          mission: true,
          debutConge: currentDate.format(DATE_FORMAT),
          finConge: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          debutConge: currentDate,
          finConge: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a StatusEmploye', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addStatusEmployeToCollectionIfMissing', () => {
      it('should add a StatusEmploye to an empty array', () => {
        const statusEmploye: IStatusEmploye = { id: 123 };
        expectedResult = service.addStatusEmployeToCollectionIfMissing([], statusEmploye);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(statusEmploye);
      });

      it('should not add a StatusEmploye to an array that contains it', () => {
        const statusEmploye: IStatusEmploye = { id: 123 };
        const statusEmployeCollection: IStatusEmploye[] = [
          {
            ...statusEmploye,
          },
          { id: 456 },
        ];
        expectedResult = service.addStatusEmployeToCollectionIfMissing(statusEmployeCollection, statusEmploye);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a StatusEmploye to an array that doesn't contain it", () => {
        const statusEmploye: IStatusEmploye = { id: 123 };
        const statusEmployeCollection: IStatusEmploye[] = [{ id: 456 }];
        expectedResult = service.addStatusEmployeToCollectionIfMissing(statusEmployeCollection, statusEmploye);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(statusEmploye);
      });

      it('should add only unique StatusEmploye to an array', () => {
        const statusEmployeArray: IStatusEmploye[] = [{ id: 123 }, { id: 456 }, { id: 93229 }];
        const statusEmployeCollection: IStatusEmploye[] = [{ id: 123 }];
        expectedResult = service.addStatusEmployeToCollectionIfMissing(statusEmployeCollection, ...statusEmployeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const statusEmploye: IStatusEmploye = { id: 123 };
        const statusEmploye2: IStatusEmploye = { id: 456 };
        expectedResult = service.addStatusEmployeToCollectionIfMissing([], statusEmploye, statusEmploye2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(statusEmploye);
        expect(expectedResult).toContain(statusEmploye2);
      });

      it('should accept null and undefined values', () => {
        const statusEmploye: IStatusEmploye = { id: 123 };
        expectedResult = service.addStatusEmployeToCollectionIfMissing([], null, statusEmploye, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(statusEmploye);
      });

      it('should return initial array if no StatusEmploye is added', () => {
        const statusEmployeCollection: IStatusEmploye[] = [{ id: 123 }];
        expectedResult = service.addStatusEmployeToCollectionIfMissing(statusEmployeCollection, undefined, null);
        expect(expectedResult).toEqual(statusEmployeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
