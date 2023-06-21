import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRoute } from "@angular/router";
import { EMPTY, of, throwError } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { Announcement } from "../model/announcement.model";
import { AnnouncementService } from "../service/announcement-service";
import { AnnouncementListResolver } from "./announcement-list.resolver";

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

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            // add this to imports array
            imports: [HttpClientTestingModule],
            providers: [
                AnnouncementListResolver,
                { provide: ActivatedRoute, useValue: { 'snapshot': activatedRouteSnapshot } },
                { provide: SplashScreenStateService, useValue: splashScreenStateService },
                { provide: AnnouncementService, useValue: service },
                { provide: SharedDataService, useValue: sharedData }
            ]
        }).compileComponents();

        resolver = TestBed.inject(AnnouncementListResolver)
    };

    beforeEach(async () => {
        splashScreenStateService = {
            start: jest.fn(),
            stop: jest.fn()
        };

        service = {
            list: jest.fn().mockReturnValue(of({ resultList: mockResponse, totalElements: mockResponse.length }))
        };

        sharedData = {
            clearData: jest.fn(),
            clearDataAndReturn: jest.fn().mockReturnValue(of(EMPTY))
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
        service.list = jest.fn().mockReturnValue(throwError(() => new Error("Oops!")));
        configureTestBed();

        // act & assert
        TestBed.runInInjectionContext(() => {
            resolver.resolve().subscribe(response => {
                // assert
                expect(response).toEqual(mockResponse);
                expect(splashScreenStateService.start).toBeCalled();
                expect(splashScreenStateService.stop).toBeCalled();
            });
            localStorage.clear();
        });
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

        TestBed.runInInjectionContext(() => {
            resolver.resolve().subscribe(response => {
                // assert
                expect(response).toEqual(mockResponse);
                expect(splashScreenStateService.start).toBeCalled();
                expect(splashScreenStateService.stop).toBeCalled();
            });
        });
    });
});