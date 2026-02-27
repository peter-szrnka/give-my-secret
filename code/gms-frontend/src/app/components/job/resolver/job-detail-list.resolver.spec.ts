import { HttpErrorResponse } from "@angular/common/http";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { firstValueFrom, of, throwError } from "rxjs";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { JobDetail } from "../model/job-detail.model";
import { JobDetailService } from "../service/job-detail.service";
import { JobDetailListResolver } from "./job-detail-list.resolver";
import { vi } from "vitest";
import { JobDetailList } from "../model/job-detail-list.model";

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
        correlationId: 'correlationId',
        status: 'COMPLETED',
        duration: 100,
        creationDate: new Date(),
        startTime: new Date()
    }];
    const mockResponseList: JobDetailList = { resultList: mockResponse, totalElements: mockResponse.length };

    beforeEach(async () => {
        splashScreenStateService = {
            start: vi.fn(),
            stop: vi.fn()
        };

        service = {
            list: vi.fn().mockReturnValue(of(mockResponseList))
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
            list: vi.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error: new Error("!"), status: 500, statusText: "Oops!" })))
        };

        configureTestBed();

        // act
        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));
                
        // assert
        expect(response).toEqual({
            "error": "!",
            "resultList": [],
            "totalElements": 0,
        });
        expect(splashScreenStateService.start).toHaveBeenCalled();
    });

    it('should handle error 2', async () => {
        activatedRouteSnapshot = {
            "params": {
                "id": "1"
            },
            "queryParams": {
                "page" : 0
            }
        };
        localStorage.setItem('apikey_pageSize', '27');
        service.list = vi.fn().mockReturnValue(throwError(() => new Error("Oops!")));
        configureTestBed();

        // act
        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));

        // assert
        expect(response).toEqual({
            "error": "!",
            "resultList": [],
            "totalElements": 0,
        });
        expect(splashScreenStateService.start).toHaveBeenCalled();

        localStorage.clear();
    });

    it('should return existing entity', async () => {
        activatedRouteSnapshot = {
            "params": {
                "id": "1"
            },
            "queryParams": {}
        };
        configureTestBed();

        // act
        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));
                
        // assert
        expect(response).toEqual(mockResponseList);
        expect(splashScreenStateService.start).toHaveBeenCalled();
    });
});