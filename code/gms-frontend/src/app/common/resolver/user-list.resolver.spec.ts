import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { of } from "rxjs";
import { UserData } from "../model/user-data.model";
import { SharedDataService } from "../service/shared-data-service";
import { SplashScreenStateService } from "../service/splash-screen-service";
import { UserService } from "../service/user-service";
import { UserListResolver } from "./user-list.resolver";

describe('UserListResolver', () => {
    let resolver : UserListResolver;
    let activatedRouteSnapshot : any;
    let splashScreenStateService : any;
    let service : any;
    let routerStateSnapshot : any;
    let sharedData : any;

    const mockResponse : UserData[] = [{
        id : 1,
        status : "ACTIVE",
        roles : []
    }];

    beforeEach(async() => {
        splashScreenStateService = {
            start : jest.fn(),
            stop : jest.fn()
        };

        service = {
            list : jest.fn().mockReturnValue(of(mockResponse))
        };

        sharedData = {
            clearData: jest.fn()
        };

        TestBed.configureTestingModule({
          // add this to imports array
          imports: [HttpClientTestingModule],
          providers: [
            UserListResolver,
            { provide : ActivatedRouteSnapshot, useValue : activatedRouteSnapshot },
            { provide: SplashScreenStateService, useValue : splashScreenStateService },
            { provide : UserService, useValue : service },
            { provide : RouterStateSnapshot, useValue : routerStateSnapshot },
            { provide : SharedDataService, useValue: sharedData }
        ]
        }).compileComponents();
    
        resolver = TestBed.inject(UserListResolver)
    })

    it('should create', () => {
        expect(resolver).toBeTruthy()
    });

    it('should return existing entity', async() => {
        const route : any = jest.fn();
        activatedRouteSnapshot = {
            "params" : {
                "id" : "1"
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