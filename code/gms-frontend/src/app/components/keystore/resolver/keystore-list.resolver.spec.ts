import { TestBed } from "@angular/core/testing";
import { firstValueFrom, of, throwError } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { Keystore } from "../model/keystore.model";
import { KeystoreService } from "../service/keystore-service";
import { KeystoreListResolver } from "./keystore-list.resolver";
import { vi } from "vitest";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { KeystoreList } from "../model/keystore-list";
import { HttpErrorResponse } from "@angular/common/http";

/**
 * @author Peter Szrnka
 */
describe('KeystoreListResolver', () => {
    let resolver: KeystoreListResolver;
    let activatedRouteSnapshot: any;
    let splashScreenStateService: any;
    let service: any;
    let sharedData: any;

    const mockResponse: Keystore[] = [{
        id: 1,
        name: "keystore",
        description: "description",
        aliases: [],
        generated: false
    }];
    const mockResponseList: KeystoreList = { resultList: mockResponse, totalElements: mockResponse.length };

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                KeystoreListResolver,
                //{ provide: ActivatedRouteSnapshot, activatedRouteSnapshot },
                { provide: SplashScreenStateService, useValue: splashScreenStateService },
                { provide: KeystoreService, useValue: service },
                { provide: SharedDataService, useValue: sharedData }
            ]
        }).compileComponents();
        resolver = TestBed.inject(KeystoreListResolver);
    };

    beforeEach(async () => {
        splashScreenStateService = {
            start: vi.fn(),
            stop: vi.fn()
        };

        service = {
            list: vi.fn().mockReturnValue(of(mockResponseList))
        };

        sharedData = {
            clearData: vi.fn()
        };
    })

    it('should create', () => {
        configureTestBed();
        expect(resolver).toBeTruthy()
    });

    it('should handle error', async () => {
        activatedRouteSnapshot = {
            "params": {
                "id": "1"
            },
            "queryParams": {}
        };
        localStorage.setItem('keystore_pageSize', '27');
        service.list = vi.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 500, statusText: "Oops!" })));
        configureTestBed();

        // act
       TestBed.runInInjectionContext(() => {
            resolver.resolve(activatedRouteSnapshot).subscribe(() => {
                // assert
                expect(splashScreenStateService.start).toHaveBeenCalled();
            });
        });
    });

    it.each([
        [25],
        [-1]
    ])('should return existing entity', async (localStorageItemSize: number) => {
        activatedRouteSnapshot = {
            "params": {
                "id": "1"
            },
            "queryParams": {
                "page": "0"
            }
        }

        if (localStorageItemSize === -1) {
            localStorage.setItem('keystore_pageSize', '25');
        }

        configureTestBed();

        // act
        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));

        // assert
        expect(response).toEqual(mockResponseList);
        expect(splashScreenStateService.start).toHaveBeenCalled();
        localStorage.removeItem('keystore_pageSize');
    });
});