import { TestBed } from "@angular/core/testing";
import { ActivatedRouteSnapshot } from "@angular/router";
import { firstValueFrom, of, throwError } from "rxjs";
import { EMPTY_KEYSTORE, Keystore } from "../model/keystore.model";
import { KeystoreService } from "../service/keystore-service";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { KeystoreDetailResolver } from "./keystore-detail.resolver";
import { vi } from "vitest";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { HttpErrorResponse } from "@angular/common/http";

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
            start : vi.fn(),
            stop : vi.fn()
        };

        service = {
            getById : vi.fn().mockReturnValue(of(mockResponse))
        };

        sharedData = {
            clearData: vi.fn()
        };

        TestBed.configureTestingModule({
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

        service.getById = vi.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 500, statusText: "Oops!" })));

        // act
        TestBed.runInInjectionContext(() => {
            resolver.resolve(activatedRouteSnapshot).subscribe(() => {
                // assert
                expect(splashScreenStateService.start).toHaveBeenCalled();
            });
        });
    });

    it('should return existing entity', async() => {
        activatedRouteSnapshot = {
            "params" : {
                "id" : "1"
            }
        }

        // act
        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));

        // assert
        expect(response).toEqual(mockResponse);
        expect(splashScreenStateService.start).toHaveBeenCalled();
    });
});