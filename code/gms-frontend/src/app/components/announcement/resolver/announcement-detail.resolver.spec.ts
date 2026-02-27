import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRouteSnapshot } from "@angular/router";
import { firstValueFrom, of, throwError } from "rxjs";
import { Announcement, EMPTY_ANNOUNCEMENT } from "../model/announcement.model";
import { AnnouncementService } from "../service/announcement-service";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { AnnouncementDetailResolver } from "./announcement-detail.resolver";
import { vi } from "vitest";

/**
 * @author Peter Szrnka
 */
describe('AnnouncementDetailResolver', () => {
    let resolver : AnnouncementDetailResolver;
    let activatedRouteSnapshot : any;
    let splashScreenStateService : any;
    let service : any;
    let sharedData : any;

    const mockResponse : Announcement = {
        id : 1,
        title: "title",
        description : "description"
    };

    beforeEach(async() => {
        splashScreenStateService = {
            start : vi.fn(),
            stop : vi.fn()
        };

        service = {
            getById : vi.fn().mockReturnValue(of(mockResponse))
        };

        sharedData = {
            clearData: vi.fn()
        };

        TestBed.configureTestingModule({
          imports: [HttpClientTestingModule],
          providers: [
            AnnouncementDetailResolver,
            { provide : ActivatedRouteSnapshot, useValue : activatedRouteSnapshot },
            { provide: SplashScreenStateService, useValue : splashScreenStateService },
            { provide : AnnouncementService, useValue : service },
            { provide : SharedDataService, useValue: sharedData }
        ]
        }).compileComponents();
        resolver = TestBed.inject(AnnouncementDetailResolver);
    });

    it('should create', () => {
        expect(resolver).toBeTruthy()
    });

    it('should return empty response', async() => {
        activatedRouteSnapshot = {
            "params" : {
                "id" : "new"
            }
        }

        TestBed.runInInjectionContext(() => {
            resolver.resolve(activatedRouteSnapshot).subscribe(response => {
                // assert
                expect(response).toEqual(EMPTY_ANNOUNCEMENT);
            });
        });
    });

    it('should handle error', async() => {
        activatedRouteSnapshot = {
            "params" : {
                "id" : "1"
            }
        }

        service.getById = vi.fn().mockReturnValue(throwError(() => new Error("Oops!")));

        // act
        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));

        // assert
        expect(response).toEqual(mockResponse);
        expect(splashScreenStateService.start).toHaveBeenCalled();
    });

    it('should return existing entity', async() => {
        activatedRouteSnapshot = {
            "params" : {
                "id" : "1"
            }
        }

        // act
        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));

        // assert
        expect(response).toEqual(mockResponse);
        expect(splashScreenStateService.start).toHaveBeenCalled();
    });
});