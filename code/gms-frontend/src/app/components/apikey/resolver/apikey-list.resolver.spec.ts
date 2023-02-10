import { HttpErrorResponse } from "@angular/common/http";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { of, throwError } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { ApiKey } from "../model/apikey.model";
import { ApiKeyService } from "../service/apikey-service";
import { ApiKeyListResolver } from "./apikey-list.resolver";

/**
 * @author Peter Szrnka
 */
describe('ApiKeyListResolver', () => {
    let resolver : ApiKeyListResolver;
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
              ApiKeyListResolver,
              { provide : ActivatedRouteSnapshot, useValue : activatedRouteSnapshot },
              { provide: SplashScreenStateService, useValue : splashScreenStateService },
              { provide : ApiKeyService, useValue : service },
              { provide : RouterStateSnapshot, useValue : routerStateSnapshot },
              { provide : SharedDataService, useValue: sharedData }
          ]
          }).compileComponents();
      
          resolver = TestBed.inject(ApiKeyListResolver)
    };

    const mockResponse : ApiKey[] = [{
        id : 1,
        name: "apiKey",
        description : "description"
    }];

    beforeEach(async() => {
        splashScreenStateService = {
            start : jest.fn(),
            stop : jest.fn()
        };

        service = {
            list : jest.fn().mockReturnValue(of(mockResponse))
        };

        sharedData = {
            clearDataAndReturn: jest.fn().mockReturnValue(of([]))
        };
    })

    it('should create', () => {
        configureTestBed();
        expect(resolver).toBeTruthy()
    });

    it('should handle error', async() => {
        const route : any = jest.fn();
        activatedRouteSnapshot = {
            "params" : {
                "id" : "1"
            }
        };

        service = {
            list : jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 500, statusText: "Oops!" })))
        };

        configureTestBed();

        resolver.resolve(activatedRouteSnapshot, route).subscribe(response => {
            // assert
            expect(response).toEqual(mockResponse);
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