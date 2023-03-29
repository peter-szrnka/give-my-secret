import { HttpErrorResponse } from "@angular/common/http";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { of, throwError } from "rxjs";
import { EMPTY_HOME_ADMIN_DATA, EMPTY_HOME_USER_DATA, HomeResolver } from "./home.resolver";
import { HomeData } from "../model/home-data.model";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { User } from "../../user/model/user.model";
import { HomeService } from "../service/home.service";

/**
 * @author Peter Szrnka
 */
describe('HomeResolver', () => {
    let resolver: HomeResolver;
    let activatedRouteSnapshot: any;
    let routerStateSnapshot: any;

    // Injected services
    let sharedDataService: any;
    let homeService : any;
    let splashScreenStateService: any;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports: [RouterTestingModule, HttpClientTestingModule],
            providers: [
                { provide: ActivatedRouteSnapshot, useValue: activatedRouteSnapshot },
                { provide: RouterStateSnapshot, useValue: routerStateSnapshot },
                { provide: SharedDataService, useValue: sharedDataService },
                { provide : HomeService, useValue: homeService },
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

        sharedDataService = {
            getUserInfo : jest.fn(),
            clearDataAndReturn : jest.fn()
        };

        homeService = {
            getData : jest.fn()
        }

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
            getUserInfo: jest.fn().mockReturnValueOnce({ roles: ["ROLE_ADMIN"] } as User),
            clearDataAndReturn : jest.fn().mockResolvedValue(EMPTY_HOME_ADMIN_DATA)
        };
        homeService = {
            getData : jest.fn().mockReturnValue(of(EMPTY_HOME_ADMIN_DATA))
        }
        configureTestBed();

        resolver.resolve(activatedRouteSnapshot, routerStateSnapshot).subscribe(() => {
            // assert
            expect(resolver).toBeTruthy();
            expect(splashScreenStateService.start).toHaveBeenCalled();
            expect(sharedDataService.getUserInfo).toHaveBeenCalled();
            expect(homeService.getData).toHaveBeenCalled();
        });
    });

    it('should return user data', () => {
        sharedDataService = {
            getUserInfo: jest.fn().mockReturnValueOnce({ roles: ["ROLE_USER"] } as User),
            clearDataAndReturn : jest.fn().mockResolvedValue(EMPTY_HOME_USER_DATA)
        };
        homeService = {
            getData : jest.fn().mockReturnValue(of(EMPTY_HOME_USER_DATA))
        }
        configureTestBed();

        resolver.resolve(activatedRouteSnapshot, routerStateSnapshot).subscribe(() => {
            // assert
            expect(resolver).toBeTruthy();
            expect(splashScreenStateService.start).toHaveBeenCalled();
            expect(sharedDataService.getUserInfo).toHaveBeenCalled();
            expect(homeService.getData).toHaveBeenCalled();
        });
    });

    it('should handle error for admin data', async() => {
        sharedDataService = {
            getUserInfo: jest.fn().mockReturnValueOnce({ roles: ["ROLE_ADMIN"] } as User),
            clearDataAndReturn : jest.fn().mockResolvedValue(EMPTY_HOME_ADMIN_DATA)
        };
        homeService = {
            getData: jest.fn().mockReturnValue(throwError(() => new Error("Error!")))
        };
        configureTestBed();

        resolver.resolve(activatedRouteSnapshot, routerStateSnapshot).subscribe((data : HomeData) => {
            // assert
            expect(resolver).toBeFalsy();
            expect(splashScreenStateService.start).toHaveBeenCalled();
            expect(sharedDataService.clearDataAndReturn).toHaveBeenCalled();
            expect(homeService.getData).toThrowError('Error!');

            expect(data).toBeTruthy();
            expect(data.events).toEqual([]);
            expect(data.userCount).toEqual(0);
        });
    });

    it('should handle error for user data', async() => {
        sharedDataService = {
            getUserInfo: jest.fn().mockReturnValue({ roles: ["ROLE_USER"] } as User),
            clearDataAndReturn : jest.fn().mockResolvedValue(EMPTY_HOME_USER_DATA)
        };
        homeService = {
            getData: jest.fn().mockReturnValue(throwError(() => new Error("Error!")))
        };
        configureTestBed();

        resolver.resolve(activatedRouteSnapshot, routerStateSnapshot).subscribe((data : HomeData) => {
            // assert
            expect(homeService.getData).toThrowError('Error!');
            expect(sharedDataService.getUserInfo).toHaveBeenCalled();
            expect(sharedDataService.clearDataAndReturn).toHaveBeenCalled();
            expect(data).toBeTruthy();
            expect(data.announcements).toEqual([]);
            expect(data.apiKeyCount).toEqual(0)
        });
    });
});