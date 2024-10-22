import { HttpErrorResponse } from "@angular/common/http";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { of, throwError } from "rxjs";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { JobDetail } from "../model/job-detail.model";
import { JobDetailService } from "../service/job-detail.service";
import { JobDetailListResolver } from "./job-detail-list.resolver";
/**
 * @author Peter Szrnka
 */
describe('JobDetailListResolver', () => {
    let resolver: JobDetailListResolver;
    let activatedRouteSnapshot: any;
    let splashScreenStateService: any;
    let service: any;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            // add this to imports array
            imports: [HttpClientTestingModule],
            providers: [
                JobDetailListResolver,
                { provide: SplashScreenStateService, useValue: splashScreenStateService },
                { provide: JobDetailService, useValue: service }
            ]
        }).compileComponents();

        resolver = TestBed.inject(JobDetailListResolver)
    };

    const mockResponse: JobDetail[] = [{
        id: 1,
        name: 'name',
        status: 'COMPLETED',
        duration: 100,
        creationDate: new Date(),
        startTime: new Date()
    }];

    beforeEach(async () => {
        splashScreenStateService = {
            start: jest.fn(),
            stop: jest.fn()
        };

        service = {
            list: jest.fn().mockReturnValue(of({ resultList: mockResponse, totalElements: mockResponse.length }))
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
                "page" : 0
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
            expect(splashScreenStateService.start).toHaveBeenCalled();
            expect(splashScreenStateService.stop).toHaveBeenCalled();
        });
    });

    it('should handle error', async () => {
        activatedRouteSnapshot = {
            "params": {
                "id": "1"
            },
            "queryParams": {
                "page" : 0
            }
        };
        localStorage.setItem('apikey_pageSize', '27');
        service.list = jest.fn().mockReturnValue(throwError(() => new Error("Oops!")));
        configureTestBed();

        // act & assert
        resolver.resolve(activatedRouteSnapshot).subscribe(response => {
            // assert
            expect(response).toEqual(mockResponse);
            expect(splashScreenStateService.start).toHaveBeenCalled();
            expect(splashScreenStateService.stop).toHaveBeenCalled();
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
            expect(splashScreenStateService.start).toHaveBeenCalled
            expect(splashScreenStateService.stop).toHaveBeenCalled();
        });
    });
});