import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { of } from "rxjs";
import { Announcement } from "../model/announcement.model";
import { AnnouncementService } from "../service/announcement-service";
import { AnnouncementListResolver } from "./announcement-list.resolver";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { SharedDataService } from "../../../common/service/shared-data-service";

/**
 * @author Peter Szrnka
 */
describe('AnnouncementListResolver', () => {
    let resolver : AnnouncementListResolver;
    let activatedRouteSnapshot : any;
    let splashScreenStateService : any;
    let service : any;
    let routerStateSnapshot : any;
    let sharedData : any;

    const mockResponse : Announcement[] = [{
        id : 1,
        title: "title",
        description : "description"
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
            AnnouncementListResolver,
            { provide : ActivatedRouteSnapshot, useValue : activatedRouteSnapshot },
            { provide: SplashScreenStateService, useValue : splashScreenStateService },
            { provide : AnnouncementService, useValue : service },
            { provide : RouterStateSnapshot, useValue : routerStateSnapshot },
            { provide : SharedDataService, useValue: sharedData }
        ]
        }).compileComponents();
    
        resolver = TestBed.inject(AnnouncementListResolver)
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