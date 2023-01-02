import { HttpErrorResponse } from "@angular/common/http";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { of, throwError } from "rxjs";
import { ApiKey, EMPTY_API_KEY } from "../model/apikey.model";
import { ApiKeyService } from "../service/apikey-service";
import { SharedDataService } from "../service/shared-data-service";
import { SplashScreenStateService } from "../service/splash-screen-service";
import { ApiKeyDetailResolver } from "./apikey-detail.resolver";
describe('ApiKeyDetailResolver', () => {
    let resolver : ApiKeyDetailResolver;
    let activatedRouteSnapshot : any;
    let splashScreenStateService : any;
    let service : any;
    let routerStateSnapshot : any;
    let sharedData : any;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            // add this to imports array
            imports: [HttpClientTestingModule],
            providers: [
              ApiKeyDetailResolver,
              { provide : ActivatedRouteSnapshot, useValue : activatedRouteSnapshot },
              { provide: SplashScreenStateService, useValue : splashScreenStateService },
              { provide : ApiKeyService, useValue : service },
              { provide : RouterStateSnapshot, useValue : routerStateSnapshot },
              { provide : SharedDataService, useValue: sharedData }
          ]
          }).compileComponents();
      
          resolver = TestBed.inject(ApiKeyDetailResolver);
    };

    const mockResponse : ApiKey = {
        id : 1,
        name : "apiKey",
        description: "Description"
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
            clearDataAndReturn: jest.fn().mockReturnValue(of({ id : 1, name : 'apikey' } as ApiKey))
        };
    })

    it('should create', () => {
        configureTestBed();
        expect(resolver).toBeTruthy()
    })

    it('should return empty response', async() => {
        const route : any = jest.fn();
        activatedRouteSnapshot = {
            "params" : {
                "id" : "new"
            }
        };
        configureTestBed();

        resolver.resolve(activatedRouteSnapshot, route).subscribe(response => {
            // assert
            expect(response).toEqual(EMPTY_API_KEY);
        });
    });

    it('should handle error', async() => {
        const route : any = jest.fn();
        activatedRouteSnapshot = {
            "params" : {
                "id" : "1"
            }
        };

        service = {
            getById : jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 500, statusText: "Oops!" })))
        };

        configureTestBed();

        resolver.resolve(activatedRouteSnapshot, route).subscribe(() => {
            // assert
            expect(splashScreenStateService.start).toBeCalled();
            expect(splashScreenStateService.stop).toBeCalled();
        });
    });

    it('should return existing entity', async() => {
        const route : any = jest.fn();
        activatedRouteSnapshot = {
            "params" : {
                "id" : "1"
            }
        };
        configureTestBed();

        resolver.resolve(activatedRouteSnapshot, route).subscribe(response => {
            // assert
            expect(response).toEqual(mockResponse);
            expect(splashScreenStateService.start).toBeCalled();
            expect(splashScreenStateService.stop).toBeCalled();
        });
    });
});