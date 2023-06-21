import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { of, throwError } from "rxjs";
import { EMPTY_KEYSTORE, Keystore } from "../model/keystore.model";
import { KeystoreService } from "../service/keystore-service";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { KeystoreDetailResolver } from "./keystore-detail.resolver";

/**
 * @author Peter Szrnka
 */
describe('KeystoreDetailResolver', () => {
    let resolver : KeystoreDetailResolver;
    let activatedRouteSnapshot : any;
    let splashScreenStateService : any;
    let service : any;
    let sharedData : any;

    const mockResponse : Keystore = {
        id: 1,
        name: "apiKey",
        description: "Description",
        aliases: [],
        generated: false
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
            KeystoreDetailResolver,
            { provide : ActivatedRouteSnapshot, useValue : activatedRouteSnapshot },
            { provide: SplashScreenStateService, useValue : splashScreenStateService },
            { provide : KeystoreService, useValue : service },
            { provide : SharedDataService, useValue: sharedData }
        ]
        }).compileComponents();
    
        resolver = TestBed.inject(KeystoreDetailResolver)
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
            expect(response).toEqual(EMPTY_KEYSTORE);
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