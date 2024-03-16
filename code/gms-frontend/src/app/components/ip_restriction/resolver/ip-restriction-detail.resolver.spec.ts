import { HttpErrorResponse } from "@angular/common/http";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRouteSnapshot } from "@angular/router";
import { of, throwError } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { IpRestrictionDetailResolver } from "./ip-restriction-detail.resolver";
import { IpRestrictionService } from "../service/ip-restriction.service";
import { EMPTY_IP_RESTRICTION, IpRestriction } from "../model/ip-restriction.model";

/**
 * @author Peter Szrnka
 */
describe('IpRestrictionDetailResolver', () => {
    let resolver : IpRestrictionDetailResolver;
    let activatedRouteSnapshot : any;
    let splashScreenStateService : any;
    let service : any;
    let sharedData : any;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            // add this to imports array
            imports: [HttpClientTestingModule],
            providers: [
                IpRestrictionDetailResolver,
              { provide : ActivatedRouteSnapshot, useValue : activatedRouteSnapshot },
              { provide: SplashScreenStateService, useValue : splashScreenStateService },
              { provide : IpRestrictionService, useValue : service },
              { provide : SharedDataService, useValue: sharedData }
          ]
          }).compileComponents();
      
          resolver = TestBed.inject(IpRestrictionDetailResolver);
    };

    const mockResponse : IpRestriction = {
        id: 1,
        ipPattern: ".*",
        allow: false
    };

    beforeEach(async() => {
        splashScreenStateService = {
            start : jest.fn(),
            stop : jest.fn()
        };

        service = {
            getById : jest.fn().mockReturnValue(of(mockResponse))
        };

        sharedData = {
            clearDataAndReturn: jest.fn().mockReturnValue(of({ id : 1 } as IpRestriction))
        };
    })

    it('should create', () => {
        configureTestBed();
        expect(resolver).toBeTruthy()
    })

    it('should return empty response', async() => {
        activatedRouteSnapshot = {
            "params" : {
                "id" : "new"
            }
        };
        configureTestBed();

        TestBed.runInInjectionContext(() => {
            resolver.resolve(activatedRouteSnapshot).subscribe(response => {
                // assert
                expect(response).toEqual(EMPTY_IP_RESTRICTION);
            });
        });
    });

    it('should handle error', async() => {
        activatedRouteSnapshot = {
            "params" : {
                "id" : "1"
            }
        };

        service = {
            getById : jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 500, statusText: "Oops!" })))
        };

        configureTestBed();

        TestBed.runInInjectionContext(() => {
            resolver.resolve(activatedRouteSnapshot).subscribe(() => {
                // assert
                expect(splashScreenStateService.start).toBeCalled();
                expect(splashScreenStateService.stop).toBeCalled();
            });
        });
    });

    it('should return existing entity', async() => {
        activatedRouteSnapshot = {
            "params" : {
                "id" : "1"
            }
        };
        configureTestBed();

        // act
        TestBed.runInInjectionContext(() => {
            resolver.resolve(activatedRouteSnapshot).subscribe(response => {
                // assert
                expect(response).toEqual(mockResponse);
                expect(splashScreenStateService.start).toBeCalled();
                expect(splashScreenStateService.stop).toBeCalled();
            });
        });
    });
});