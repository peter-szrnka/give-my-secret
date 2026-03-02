import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRouteSnapshot } from "@angular/router";
import { firstValueFrom, of, throwError } from "rxjs";
import { EMPTY_SECRET, Secret } from "../model/secret.model";
import { SecretService } from "../service/secret-service";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { SecretDetailResolver } from "./secret-detail.resolver";
import { vi } from "vitest";
import { HttpErrorResponse } from "@angular/common/http";

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

        // act
        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));

        // assert
        expect(response).toEqual(EMPTY_SECRET);
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
        expect(response).toEqual({
            "id": 1,
            "apiKeyRestrictions": [],
            "rotationPeriod": "",
            "status": "ACTIVE",
            "type": "CREDENTIAL",
            "value": "test"
        });
        expect(splashScreenStateService.start).toHaveBeenCalled();
    });
});