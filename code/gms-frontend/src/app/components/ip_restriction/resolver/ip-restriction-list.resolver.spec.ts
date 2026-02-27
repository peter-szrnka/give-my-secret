import { HttpErrorResponse } from "@angular/common/http";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { firstValueFrom, of, throwError } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { IpRestrictionListResolver } from "./ip-restriction-list.resolver";
import { IpRestrictionService } from "../service/ip-restriction.service";
import { IpRestriction } from "../model/ip-restriction.model";
import { vi } from "vitest";
import { IpRestrictionList } from "../model/ip-restriction-list.model";
/**
 * @author Peter Szrnka
 */
describe('IpRestrictionListResolver', () => {
    let resolver: IpRestrictionListResolver;
    let activatedRouteSnapshot: any;
    let splashScreenStateService: any;
    let service: any;
    let sharedData: any;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                IpRestrictionListResolver,
                { provide: SplashScreenStateService, useValue: splashScreenStateService },
                { provide: IpRestrictionService, useValue: service },
                { provide: SharedDataService, useValue: sharedData }
            ]
        }).compileComponents();

        resolver = TestBed.inject(IpRestrictionListResolver)
    };

    const mockResponse: IpRestriction[] = [{
        id: 1,
        ipPattern: '.*',
        allow: true
    }];
    const mockResponseList: IpRestrictionList = { resultList: mockResponse, totalElements: mockResponse.length };

    beforeEach(async () => {
        splashScreenStateService = {
            start: vi.fn(),
            stop: vi.fn()
        };

        service = {
            list: vi.fn().mockReturnValue(of(mockResponseList))
        };

        sharedData = {
            clearDataAndReturn: vi.fn().mockReturnValue(of([]))
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
            "queryParams": {
            }
        };

        service = {
            list: vi.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error: new Error("!"), status: 500, statusText: "Oops!" })))
        };

        configureTestBed();

        // act & assert
        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));

        // assert
        expect(response).toEqual({
            "error": "!",
            "resultList": [],
            "totalElements": 0,
        });
        expect(splashScreenStateService.start).toHaveBeenCalled();
    });

    it('should handle error', async () => {
        activatedRouteSnapshot = {
            "params": {
                "id": "1"
            },
            "queryParams": {}
        };
        localStorage.setItem('apikey_pageSize', '27');
        service.list = vi.fn().mockReturnValue(throwError(() => new Error("Oops!")));
        configureTestBed();

        // act
        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));

        // assert
        expect(response).toEqual(mockResponse);
        expect(splashScreenStateService.start).toHaveBeenCalled();

        localStorage.clear();
    });

    it('should return existing entity', async () => {
        activatedRouteSnapshot = {
            "params": {
                "id": "1"
            },
            "queryParams": {
                "page": "0"
            }
        };
        configureTestBed();

        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));

        // assert
        expect(response).toEqual(mockResponseList);
        expect(splashScreenStateService.start).toHaveBeenCalled();
    });
});