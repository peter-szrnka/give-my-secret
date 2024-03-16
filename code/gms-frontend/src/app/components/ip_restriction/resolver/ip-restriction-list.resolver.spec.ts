import { HttpErrorResponse } from "@angular/common/http";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { of, throwError } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { IpRestrictionListResolver } from "./ip-restriction-list.resolver";
import { IpRestrictionService } from "../service/ip-restriction.service";
import { IpRestriction } from "../model/ip-restriction.model";
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
            // add this to imports array
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

    beforeEach(async () => {
        splashScreenStateService = {
            start: jest.fn(),
            stop: jest.fn()
        };

        service = {
            list: jest.fn().mockReturnValue(of({ resultList: mockResponse, totalElements: mockResponse.length }))
        };

        sharedData = {
            clearDataAndReturn: jest.fn().mockReturnValue(of([]))
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
            list: jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error: new Error("!"), status: 500, statusText: "Oops!" })))
        };

        configureTestBed();

        // act & assert
        resolver.resolve(activatedRouteSnapshot).subscribe(response => {
            // assert
            expect(response).toEqual(mockResponse);
            expect(splashScreenStateService.start).toBeCalled();
            expect(splashScreenStateService.stop).toBeCalled();
        });
    });

    it('should handle error', async () => {
        activatedRouteSnapshot = {
            "params": {
                "id": "1"
            },
            "queryParams": {}
        };
        localStorage.setItem('apikey_pageSize', '27');
        service.list = jest.fn().mockReturnValue(throwError(() => new Error("Oops!")));
        configureTestBed();

        // act & assert
        resolver.resolve(activatedRouteSnapshot).subscribe(response => {
            // assert
            expect(response).toEqual(mockResponse);
            expect(splashScreenStateService.start).toBeCalled();
            expect(splashScreenStateService.stop).toBeCalled();
        });

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

        resolver.resolve(activatedRouteSnapshot).subscribe(response => {
            // assert
            expect(response).toEqual(mockResponse);
            expect(splashScreenStateService.start).toBeCalled();
            expect(splashScreenStateService.stop).toBeCalled();
        });
    });
});