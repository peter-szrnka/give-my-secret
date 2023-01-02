import { HttpErrorResponse } from "@angular/common/http";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { of } from "rxjs";
import { Event } from "../model/event.model";
import { HomeData } from "../model/home-data.model";
import { User } from "../model/user.model";
import { AnnouncementService } from "../service/announcement-service";
import { ApiKeyService } from "../service/apikey-service";
import { EventService } from "../service/event-service";
import { KeystoreService } from "../service/keystore-service";
import { SharedDataService } from "../service/shared-data-service";
import { SplashScreenStateService } from "../service/splash-screen-service";
import { UserService } from "../service/user-service";
import { HomeResolver } from "./home.resolver";

describe('HomeResolver', () => {
    let resolver: HomeResolver;
    let activatedRouteSnapshot: any;
    let routerStateSnapshot: any;

    // Injected services
    let sharedDataService: any;
    let eventService: any;
    let userService: any;
    let annoucementService: any;
    let apiKeyService: any;
    let keystoreService: any;
    let splashScreenStateService: any;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            // add this to imports array
            imports: [RouterTestingModule, HttpClientTestingModule],
            providers: [
                //HomeResolver,
                { provide: ActivatedRouteSnapshot, useValue: activatedRouteSnapshot },
                { provide: RouterStateSnapshot, useValue: routerStateSnapshot },
                { provide: SharedDataService, useValue: sharedDataService },
                { provide: EventService, useValue: eventService },
                { provide: UserService, useValue: userService },
                { provide: AnnouncementService, useValue: annoucementService },
                { provide: ApiKeyService, useValue: apiKeyService },
                { provide: KeystoreService, useValue: keystoreService },
                { provide: SplashScreenStateService, useValue: splashScreenStateService }
            ]
        }).compileComponents();

        resolver = TestBed.inject(HomeResolver);
    };

    beforeEach(() => {
        splashScreenStateService = {
            start: jest.fn(),
            stop: jest.fn()
        };

        annoucementService = {
            list: jest.fn().mockReturnValue(of([]))
        };
        apiKeyService = {
            count: jest.fn().mockReturnValue(of(1))
        };

        eventService = {
            list: jest.fn().mockImplementation(() => {
                return of([
                    { id: 1, operation: "SAVE", target: "KEYSTORE", userId: "user-1" } as Event,
                    { id: 2, operation: "SAVE", target: "KEYSTORE", userId: "user-2" } as Event,
                ]);
            })
        }

        keystoreService = {
            count: jest.fn().mockReturnValue(of(1))
        };

        userService = {
            count: jest.fn().mockReturnValue(of(1))
        };

        sharedDataService = {
            getUserInfo : jest.fn()
        };

        activatedRouteSnapshot = { "params": {} };
        routerStateSnapshot = {};
    })

    it('should throw error', () => {
        sharedDataService = {
            getUserInfo: jest.fn().mockReturnValue(undefined)
        };
        configureTestBed();

        resolver.resolve(activatedRouteSnapshot, routerStateSnapshot).subscribe((response : any) => {
            // assert
            expect(response).toBeTruthy();
        });
    });

    it('should return admin data', () => {
        sharedDataService = {
            getUserInfo: jest.fn().mockReturnValueOnce({ roles: ["ROLE_ADMIN"] } as User)
        };
        configureTestBed();

        resolver.resolve(activatedRouteSnapshot, routerStateSnapshot).subscribe(() => {
            // assert
            expect(resolver).toBeTruthy();
            expect(splashScreenStateService.start).toHaveBeenCalled();
            expect(sharedDataService.getUserInfo).toHaveBeenCalled();
            expect(eventService.count).toHaveBeenCalled();
            expect(userService.count).toHaveBeenCalled();
        });
    });

    it('should return user data', () => {
        sharedDataService = {
            getUserInfo: jest.fn().mockReturnValueOnce({ roles: ["ROLE_USER"] } as User)
        };
        configureTestBed();

        resolver.resolve(activatedRouteSnapshot, routerStateSnapshot).subscribe(() => {
            // assert
            expect(resolver).toBeTruthy();
            expect(splashScreenStateService.start).toHaveBeenCalled();
            expect(sharedDataService.getUserInfo).toHaveBeenCalled();
            expect(annoucementService.count).toHaveBeenCalled();
            expect(apiKeyService.count).toHaveBeenCalled();
            expect(keystoreService.count).toHaveBeenCalled();
        });
    });

    it('should handle error for admin data', async() => {
        sharedDataService = {
            getUserInfo: jest.fn().mockReturnValueOnce({ roles: ["ROLE_ADMIN"] } as User)
        };
        userService = {
            count: jest.fn().mockReturnValue(new HttpErrorResponse({ error: new Error("Error!"), status: 403, statusText: "Error!" }))
        }
        configureTestBed();

        resolver.resolve(activatedRouteSnapshot, routerStateSnapshot).subscribe((data : HomeData) => {
            // assert
            expect(resolver).toBeFalsy();
            expect(splashScreenStateService.start).toHaveBeenCalled();
            expect(sharedDataService.clearData).toHaveBeenCalled();
            expect(eventService.count).toHaveBeenCalled();
            expect(userService.count).toThrowError('Error!');

            expect(data).toBeTruthy();
            expect(data.latestEvents).toEqual([]);
            expect(data.userCount).toEqual(0);
        });
    });

    it('should handle error for user data', async() => {
        sharedDataService = {
            getUserInfo: jest.fn().mockReturnValue({ roles: ["ROLE_USER"] } as User)
        };
        apiKeyService = {
            count: jest.fn().mockReturnValue(new HttpErrorResponse(({ error: new Error("Error!"), status: 403, statusText: "Error!" })))
        };
        configureTestBed();

        resolver.resolve(activatedRouteSnapshot, routerStateSnapshot).subscribe((data : HomeData) => {
            // assert
            expect(apiKeyService.count).toHaveBeenCalledTimes(2);
            expect(sharedDataService.getUserInfo).toHaveBeenCalled();
            expect(sharedDataService.clearData).toHaveBeenCalled();
            expect(data).toEqual(null);
        });
    });
});