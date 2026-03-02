import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { EMPTY, firstValueFrom, of, throwError } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { Announcement } from "../model/announcement.model";
import { AnnouncementService } from "../service/announcement-service";
import { AnnouncementListResolver } from "./announcement-list.resolver";
import { vi } from "vitest";
import { AnnouncementList } from "../model/annoucement-list.model";
import { HttpErrorResponse } from "@angular/common/http";

/**
 * @author Peter Szrnka
 */
describe('AnnouncementListResolver', () => {
    let resolver: AnnouncementListResolver;
    let activatedRouteSnapshot: any;
    let splashScreenStateService: any;
    let service: any;
    let sharedData: any;

    const mockResponse: Announcement[] = [{
        id: 1,
        title: "title",
        description: "description"
    }];
    const mockResponseList: AnnouncementList = {
        resultList: mockResponse,
        totalElements: mockResponse.length
    };

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                AnnouncementListResolver,
                //{ provide: ActivatedRouteSnapshot, activatedRouteSnapshot },
                { provide: SplashScreenStateService, useValue: splashScreenStateService },
                { provide: AnnouncementService, useValue: service },
                { provide: SharedDataService, useValue: sharedData }
            ]
        }).compileComponents();

        resolver = TestBed.inject(AnnouncementListResolver)
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
            clearData: vi.fn(),
            clearDataAndReturn: vi.fn().mockReturnValue(of(EMPTY))
        };
    });

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
        localStorage.setItem('announcement_pageSize', '27');
        service.list = vi.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error : new Error("!"), status : 500, statusText: "Oops!" })));
        configureTestBed();

        // act
        TestBed.runInInjectionContext(() => {
            resolver.resolve(activatedRouteSnapshot).subscribe(() => {
                // assert
                expect(splashScreenStateService.start).toHaveBeenCalled();
            });
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

        // act
        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));

        // assert
        expect(response).toEqual(mockResponseList);
        expect(splashScreenStateService.start).toHaveBeenCalled();
    });
});