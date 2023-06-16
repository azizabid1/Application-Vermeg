import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IStatusEmploye, StatusEmploye } from '../status-employe.model';
import { StatusEmployeService } from '../service/status-employe.service';

import { StatusEmployeRoutingResolveService } from './status-employe-routing-resolve.service';

describe('StatusEmploye routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: StatusEmployeRoutingResolveService;
  let service: StatusEmployeService;
  let resultStatusEmploye: IStatusEmploye | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(StatusEmployeRoutingResolveService);
    service = TestBed.inject(StatusEmployeService);
    resultStatusEmploye = undefined;
  });

  describe('resolve', () => {
    it('should return IStatusEmploye returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultStatusEmploye = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultStatusEmploye).toEqual({ id: 123 });
    });

    it('should return new IStatusEmploye if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultStatusEmploye = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultStatusEmploye).toEqual(new StatusEmploye());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as StatusEmploye })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultStatusEmploye = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultStatusEmploye).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
