import { HttpErrorResponse } from "@angular/common/http";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { firstValueFrom, of, throwError } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { SystemProperty } from "../model/system-property.model";
import { SystemPropertyService } from "../service/system-property.service";
import { SystemPropertyListResolver } from "./system-property-list.resolver";
import { vi } from "vitest";

/**
 * @author Peter Szrnka
 */
describe('SystemPropertyListResolver', () => {
    let resolver: SystemPropertyListResolver;
    let activatedRouteSnapshot: any;
    let splashScreenStateService: any;
    let service: any;
    let sharedData: any;

    const mockResponse: SystemProperty[] = [
        { key: 'PROPERTY1', value: 'true', type: 'boolean', factoryValue: false, category: 'GENERAL' },
        { key: 'PROPERTY2', value: '10', type: 'long', factoryValue: true, category: 'GENERAL' }
    ];
    const mockResponseList = { resultList: mockResponse, totalElements: mockResponse.length };

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                SystemPropertyListResolver,
                { provide: SplashScreenStateService, useValue: splashScreenStateService },
                { provide: SystemPropertyService, useValue: service },
                { provide: SharedDataService, useValue: sharedData }
            ]
        }).compileComponents();

        resolver = TestBed.inject(SystemPropertyListResolver);
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

        // assert
        expect(resolver).toBeTruthy();
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
        };
        configureTestBed();

        if (localStorageItemSize === -1) {
            localStorage.setItem('system_property_pageSize', '25');
        }

        // act
        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));
        
        // assert
        expect(response).toEqual(mockResponseList);
        expect(splashScreenStateService.start).toHaveBeenCalled();

        localStorage.removeItem('system_property_pageSize');
    });

    it('should handle error', async () => {
        activatedRouteSnapshot = {
            "params": {
                "id": "1"
            },
            "queryParams": {}
        };

        service = {
            list: vi.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error: new Error("!"), status: 500, statusText: "Oops!" })))
        };

        configureTestBed();

        // act
        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));
        
        // assert
        expect(response).toEqual({
            totalElements: 0, resultList: []
        });
        expect(splashScreenStateService.start).toHaveBeenCalled();
    });
});