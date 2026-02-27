import { TestBed } from "@angular/core/testing";
import { firstValueFrom, of } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { UserData } from "../model/user-data.model";
import { UserService } from "../service/user-service";
import { UserListResolver } from "./user-list.resolver";
import { vi } from "vitest";
import { HttpClientTestingModule } from "@angular/common/http/testing";

/**
 * @author Peter Szrnka
 */
describe('UserListResolver', () => {
    let resolver: UserListResolver;
    let activatedRouteSnapshot: any;
    let splashScreenStateService: any;
    let service: any;
    let sharedData: any;

    const mockResponse: UserData[] = [{
        id: 1,
        status: "ACTIVE",
        role: 'ROLE_USER'
    }];

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                UserListResolver,
                { provide: SplashScreenStateService, useValue: splashScreenStateService },
                { provide: UserService, useValue: service },
                { provide: SharedDataService, useValue: sharedData }
            ]
        }).compileComponents();
        resolver = TestBed.inject(UserListResolver);
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

        // act
        const response = firstValueFrom(resolver.resolve(activatedRouteSnapshot));

        // assert
        // TODO expect(response).toEqual(mockResponse);
        expect(splashScreenStateService.start).toHaveBeenCalled();
    });
});