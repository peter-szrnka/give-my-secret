import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { of } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { EMPTY_USER_DATA, UserData } from "../model/user-data.model";
import { UserService } from "../service/user-service";
import { UserDetailResolver } from "./user-detail.resolver";
import { ActivatedRouteSnapshot } from "@angular/router";

/**
 * @author Peter Szrnka
 */
describe('UserDetailResolver', () => {
    let resolver: UserDetailResolver;
    let activatedRouteSnapshot: any;
    let splashScreenStateService: any;
    let service: any;
    let sharedData: any;

    const mockResponse: UserData = {
        role: 'ROLE_USER'
    };

    beforeEach(async () => {
        splashScreenStateService = {
            start: jest.fn(),
            stop: jest.fn()
        };

        service = {
            getById: jest.fn().mockReturnValue(of(mockResponse))
        };

        sharedData = {
            clearData: jest.fn()
        };

        TestBed.configureTestingModule({
            // add this to imports array
            imports: [HttpClientTestingModule],
            providers: [
                UserDetailResolver,
                { provide: ActivatedRouteSnapshot, useValue: activatedRouteSnapshot },
                { provide: SplashScreenStateService, useValue: splashScreenStateService },
                { provide: UserService, useValue: service },
                { provide: SharedDataService, useValue: sharedData }
            ]
        }).compileComponents();

        resolver = TestBed.inject(UserDetailResolver);
    })

    it('should create', () => {
        expect(resolver).toBeTruthy()
    })

    it('should return empty response', async () => {
        activatedRouteSnapshot = {
            "params": {
                "id": "new"
            }
        };

        TestBed.runInInjectionContext(() => {
            resolver.resolve(activatedRouteSnapshot).subscribe(response => {
                // assert
                expect(response).toEqual(EMPTY_USER_DATA);
            });
        });
    });

    it('should return existing entity', async () => {
        activatedRouteSnapshot = {
            "params": {
                "id": "1"
            }
        };

        TestBed.runInInjectionContext(() => {
            resolver.resolve(activatedRouteSnapshot).subscribe(response => {
                // assert
                expect(response).toEqual(mockResponse);
                expect(splashScreenStateService.start).toBeCalled();
                expect(splashScreenStateService.stop).toBeCalled();
            });
        });
    });
});