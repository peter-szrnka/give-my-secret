import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRoute, ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { of } from "rxjs";
import { UserData } from "../model/user-data.model";
import { UserService } from "../service/user-service";
import { UserListResolver } from "./user-list.resolver";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { SharedDataService } from "../../../common/service/shared-data-service";

/**
 * @author Peter Szrnka
 */
describe('UserListResolver', () => {
    let resolver : UserListResolver;
    let activatedRouteSnapshot : any;
    let splashScreenStateService : any;
    let service : any;
    let sharedData : any;

    const mockResponse : UserData[] = [{
        id : 1,
        status : "ACTIVE",
        roles : []
    }];

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            // add this to imports array
            imports: [HttpClientTestingModule],
            providers: [
              UserListResolver,
              { provide : ActivatedRoute, useValue : { 'snapshot' : activatedRouteSnapshot } },
              { provide: SplashScreenStateService, useValue : splashScreenStateService },
              { provide : UserService, useValue : service },
              { provide : SharedDataService, useValue: sharedData }
          ]
          }).compileComponents();
          resolver = TestBed.inject(UserListResolver);
    };

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
    })

    it('should create', () => {
        configureTestBed();
        expect(resolver).toBeTruthy()
    });

    it('should return existing entity', async() => {
        activatedRouteSnapshot = {
            "params" : {
                "id" : "1"
            },
            "queryParams" : {
                "page" : "0"
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