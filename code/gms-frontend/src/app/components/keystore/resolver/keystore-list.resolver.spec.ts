import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { of } from "rxjs";
import { Keystore } from "../model/keystore.model";
import { KeystoreService } from "../service/keystore-service";
import { KeystoreListResolver } from "./keystore-list.resolver";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { SharedDataService } from "../../../common/service/shared-data-service";

/**
 * @author Peter Szrnka
 */
describe('KeystoreListResolver', () => {
    let resolver : KeystoreListResolver;
    let activatedRouteSnapshot : any;
    let splashScreenStateService : any;
    let service : any;
    let routerStateSnapshot : any;
    let sharedData : any;

    const mockResponse : Keystore[] = [{
        id : 1,
        name: "keystore",
        description : "description",
        aliases : [],
        generated: false
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
            KeystoreListResolver,
            { provide : ActivatedRouteSnapshot, useValue : activatedRouteSnapshot },
            { provide: SplashScreenStateService, useValue : splashScreenStateService },
            { provide : KeystoreService, useValue : service },
            { provide : RouterStateSnapshot, useValue : routerStateSnapshot },
            { provide : SharedDataService, useValue: sharedData }
        ]
        }).compileComponents();
    
        resolver = TestBed.inject(KeystoreListResolver)
    })

    it('should create', () => {
        expect(resolver).toBeTruthy()
    });

    it.each([
        [25],
        [-1]
    ])('should return existing entity', async(localStorageItemSize : number) => {
        const route : any = jest.fn();
        activatedRouteSnapshot = {
            "params" : {
                "id" : "1"
            },
            "queryParams" : {
                "page" : "0"
            }
        }

        if (localStorageItemSize === -1) {
            localStorage.setItem('keystore_pageSize', '25');
        }

        resolver.resolve(activatedRouteSnapshot, route).subscribe(response => {
            // assert
            expect(response).toEqual(mockResponse);
            expect(splashScreenStateService.start).toBeCalled();
            expect(splashScreenStateService.stop).toBeCalled();
        });

        localStorage.removeItem('keystore_pageSize');
    });
});