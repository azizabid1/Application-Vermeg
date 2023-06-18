import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEquipe, Equipe } from '../equipe.model';

import { EquipeService } from './equipe.service';

describe('Equipe Service', () => {
  let service: EquipeService;
  let httpMock: HttpTestingController;
  let elemDefault: IEquipe;
  let expectedResult: IEquipe | IEquipe[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EquipeService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nom: 'AAAAAAA',
      nombrePersonne: 0,
      userUuid: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Equipe', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Equipe()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Equipe', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          nombrePersonne: 1,
          userUuid: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Equipe', () => {
      const patchObject = Object.assign(
        {
          nom: 'BBBBBB',
          userUuid: 'BBBBBB',
        },
        new Equipe()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Equipe', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          nombrePersonne: 1,
          userUuid: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Equipe', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addEquipeToCollectionIfMissing', () => {
      it('should add a Equipe to an empty array', () => {
        const equipe: IEquipe = { id: 123 };
        expectedResult = service.addEquipeToCollectionIfMissing([], equipe);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(equipe);
      });

      it('should not add a Equipe to an array that contains it', () => {
        const equipe: IEquipe = { id: 123 };
        const equipeCollection: IEquipe[] = [
          {
            ...equipe,
          },
          { id: 456 },
        ];
        expectedResult = service.addEquipeToCollectionIfMissing(equipeCollection, equipe);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Equipe to an array that doesn't contain it", () => {
        const equipe: IEquipe = { id: 123 };
        const equipeCollection: IEquipe[] = [{ id: 456 }];
        expectedResult = service.addEquipeToCollectionIfMissing(equipeCollection, equipe);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(equipe);
      });

      it('should add only unique Equipe to an array', () => {
        const equipeArray: IEquipe[] = [{ id: 123 }, { id: 456 }, { id: 36881 }];
        const equipeCollection: IEquipe[] = [{ id: 123 }];
        expectedResult = service.addEquipeToCollectionIfMissing(equipeCollection, ...equipeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const equipe: IEquipe = { id: 123 };
        const equipe2: IEquipe = { id: 456 };
        expectedResult = service.addEquipeToCollectionIfMissing([], equipe, equipe2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(equipe);
        expect(expectedResult).toContain(equipe2);
      });

      it('should accept null and undefined values', () => {
        const equipe: IEquipe = { id: 123 };
        expectedResult = service.addEquipeToCollectionIfMissing([], null, equipe, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(equipe);
      });

      it('should return initial array if no Equipe is added', () => {
        const equipeCollection: IEquipe[] = [{ id: 123 }];
        expectedResult = service.addEquipeToCollectionIfMissing(equipeCollection, undefined, null);
        expect(expectedResult).toEqual(equipeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
