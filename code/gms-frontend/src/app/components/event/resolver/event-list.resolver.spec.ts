import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { of } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { Event } from "../model/event.model";
import { EventService } from "../service/event-service";
import { EventListResolver } from "./event-list.resolver";

/**
 * @author Peter Szrnka
 */
describe('EventListResolver', () => {
    let resolver : EventListResolver;
    let activatedRouteSnapshot : any;
    let splashScreenStateService : any;
    let service : any;
    let routerStateSnapshot : any;
    let sharedData : any;

    const mockResponse : Event[] = [{
        id : 1,
        username : "user-1",
        operation : "SAVE",
        target : "EVENT",
        eventDate: new Date()
    }];

    beforeEach(async() => {
        splashScreenStateService = {
            start : jest.fn(),
            stop : jest.fn()
        };

        service = {
            list : jest.fn().mockReturnValue(of({ resultList : mockResponse, totalElements : mockResponse.length }))
        };

        sharedData = {
            clearData: jest.fn()
        };

        TestBed.configureTestingModule({
          // add this to imports array
          imports: [HttpClientTestingModule],
          providers: [
            EventListResolver,
            { provide : ActivatedRouteSnapshot, useValue : activatedRouteSnapshot },
            { provide: SplashScreenStateService, useValue : splashScreenStateService },
            { provide : EventService, useValue : service },
            { provide : RouterStateSnapshot, useValue : routerStateSnapshot },
            { provide : SharedDataService, useValue: sharedData }
        ]
        }).compileComponents();
    
        resolver = TestBed.inject(EventListResolver)
    })

    it('should create', () => {
        expect(resolver).toBeTruthy()
    });

    it('should return existing entity', async() => {
        const route : any = jest.fn();
        activatedRouteSnapshot = {
            "params" : {
                "id" : "1"
            },
            "queryParams" : {
                "page" : "0"
            }
        }

        resolver.resolve(activatedRouteSnapshot, route).subscribe(response => {
            // assert
            expect(response).toEqual(mockResponse);
            expect(splashScreenStateService.start).toBeCalled();
            expect(splashScreenStateService.stop).toBeCalled();
        });
    });
});