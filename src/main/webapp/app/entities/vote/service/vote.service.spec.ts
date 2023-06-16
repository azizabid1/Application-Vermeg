import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { Rendement } from 'app/entities/enumerations/rendement.model';
import { IVote, Vote } from '../vote.model';

import { VoteService } from './vote.service';

describe('Vote Service', () => {
  let service: VoteService;
  let httpMock: HttpTestingController;
  let elemDefault: IVote;
  let expectedResult: IVote | IVote[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(VoteService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      typeVote: Rendement.FAIBLE,
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

    it('should create a Vote', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Vote()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Vote', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          typeVote: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Vote', () => {
      const patchObject = Object.assign(
        {
          typeVote: 'BBBBBB',
        },
        new Vote()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Vote', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          typeVote: 'BBBBBB',
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

    it('should delete a Vote', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addVoteToCollectionIfMissing', () => {
      it('should add a Vote to an empty array', () => {
        const vote: IVote = { id: 123 };
        expectedResult = service.addVoteToCollectionIfMissing([], vote);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(vote);
      });

      it('should not add a Vote to an array that contains it', () => {
        const vote: IVote = { id: 123 };
        const voteCollection: IVote[] = [
          {
            ...vote,
          },
          { id: 456 },
        ];
        expectedResult = service.addVoteToCollectionIfMissing(voteCollection, vote);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Vote to an array that doesn't contain it", () => {
        const vote: IVote = { id: 123 };
        const voteCollection: IVote[] = [{ id: 456 }];
        expectedResult = service.addVoteToCollectionIfMissing(voteCollection, vote);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(vote);
      });

      it('should add only unique Vote to an array', () => {
        const voteArray: IVote[] = [{ id: 123 }, { id: 456 }, { id: 60868 }];
        const voteCollection: IVote[] = [{ id: 123 }];
        expectedResult = service.addVoteToCollectionIfMissing(voteCollection, ...voteArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const vote: IVote = { id: 123 };
        const vote2: IVote = { id: 456 };
        expectedResult = service.addVoteToCollectionIfMissing([], vote, vote2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(vote);
        expect(expectedResult).toContain(vote2);
      });

      it('should accept null and undefined values', () => {
        const vote: IVote = { id: 123 };
        expectedResult = service.addVoteToCollectionIfMissing([], null, vote, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(vote);
      });

      it('should return initial array if no Vote is added', () => {
        const voteCollection: IVote[] = [{ id: 123 }];
        expectedResult = service.addVoteToCollectionIfMissing(voteCollection, undefined, null);
        expect(expectedResult).toEqual(voteCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
