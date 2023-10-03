import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRouteSnapshot } from "@angular/router";
import { of, throwError } from "rxjs";
import { Secret } from "../model/secret.model";
import { EMPTY_USER } from "../../user/model/user.model";
import { SecretService } from "../service/secret-service";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { SecretDetailResolver } from "./secret-detail.resolver";

/**
 * @author Peter Szrnka
 */
describe('SecretDetailResolver', () => {
    let resolver : SecretDetailResolver;
    let activatedRouteSnapshot : any;
    let splashScreenStateService : any;
    let service : any;
    let sharedData : any;

    const mockResponse : Secret = {
        id: 1,
        status: "ACTIVE",
        value: "test",
        rotationPeriod: "",
        apiKeyRestrictions: [],
        type : 'CREDENTIAL'
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
            clearData: jest.fn()
        };

        TestBed.configureTestingModule({
          // add this to imports array
          imports: [HttpClientTestingModule],
          providers: [
            SecretDetailResolver,
            { provide : ActivatedRouteSnapshot, useValue : activatedRouteSnapshot },
            { provide: SplashScreenStateService, useValue : splashScreenStateService },
            { provide : SecretService, useValue : service },
            { provide : SharedDataService, useValue: sharedData }
        ]
        }).compileComponents();
    
        resolver = TestBed.inject(SecretDetailResolver)
    })

    it('should create', () => {
        expect(resolver).toBeTruthy()
    })

    it('should return empty response', async() => {
        activatedRouteSnapshot = {
            "params" : {
                "id" : "new"
            }
        }

        resolver.resolve(activatedRouteSnapshot).subscribe(response => {
            // assert
            expect(response).toEqual(EMPTY_USER);
        });
    });

    it('should handle error', async() => {
        activatedRouteSnapshot = {
            "params" : {
                "id" : "1"
            }
        }

        service.getById = jest.fn().mockReturnValue(throwError(() => new Error("Oops!")));

        TestBed.runInInjectionContext(() => {
            resolver.resolve(activatedRouteSnapshot).subscribe(response => {
                // assert
                expect(response).toEqual(mockResponse);
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
        }

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