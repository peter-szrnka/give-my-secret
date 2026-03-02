import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { firstValueFrom, of } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { Event } from "../model/event.model";
import { EventService } from "../service/event-service";
import { EventListResolver } from "./event-list.resolver";
import { vi } from "vitest";

/**
 * @author Peter Szrnka
 */
describe('EventListResolver', () => {
    let resolver: EventListResolver;
    let activatedRouteSnapshot: any;
    let splashScreenStateService: any;
    let service: any;
    let sharedData: any;

    const mockResponse: Event[] = [{
        id: 1,
        entityId: 1,
        username: "user-1",
        operation: "SAVE",
        source: "UI",
        target: "EVENT",
        eventDate: new Date()
    }];

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                EventListResolver,
                { provide: SplashScreenStateService, useValue: splashScreenStateService },
                { provide: EventService, useValue: service },
                { provide: SharedDataService, useValue: sharedData }
            ]
        }).compileComponents();

        resolver = TestBed.inject(EventListResolver);
    };

    beforeEach(async () => {
        splashScreenStateService = {
            start: vi.fn(),
            stop: vi.fn()
        };

        service = {
            list: vi.fn().mockReturnValue(of({ resultList: mockResponse, totalElements: mockResponse.length }))
        };

        sharedData = {
            clearData: vi.fn()
        };
    })

    it('should create', () => {
        configureTestBed();
        expect(resolver).toBeTruthy()
    });

    it('should return existing entity', async () => {
        activatedRouteSnapshot = {
            params: {
                "id": "1"
            },
            queryParams: {
                "page": "0"
            }
        };
        configureTestBed();

        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));

        expect(splashScreenStateService.start).toHaveBeenCalled();
        // TODO
        //expect(response.resultList).toEqual(mockResponse);
    });
});