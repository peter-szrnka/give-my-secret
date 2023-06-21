import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRoute, ActivatedRouteSnapshot } from "@angular/router";
import { of, throwError } from "rxjs";
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
    let sharedData : any;

    const mockResponse : Keystore[] = [{
        id : 1,
        name: "keystore",
        description : "description",
        aliases : [],
        generated: false
    }];

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            // add this to imports array
            imports: [HttpClientTestingModule],
            providers: [
              KeystoreListResolver,
              { provide: ActivatedRoute, useValue: { 'snapshot': activatedRouteSnapshot } },
              { provide: SplashScreenStateService, useValue : splashScreenStateService },
              { provide : KeystoreService, useValue : service },
              { provide : SharedDataService, useValue: sharedData }
          ]
          }).compileComponents();
          resolver = TestBed.inject(KeystoreListResolver);
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

    it('should handle error', async () => {
        activatedRouteSnapshot = {
            "params": {
                "id": "1"
            },
            "queryParams": {}
        };
        localStorage.setItem('keystore_pageSize', '27');
        service.list = jest.fn().mockReturnValue(throwError(() => new Error("Oops!")));
        configureTestBed();

        // act & assert
        TestBed.runInInjectionContext(() => {
            resolver.resolve().subscribe(response => {
                // assert
                expect(response).toEqual(mockResponse);
                expect(splashScreenStateService.start).toBeCalled();
                expect(splashScreenStateService.stop).toBeCalled();
            });
            localStorage.clear();
        });
    });

    it.each([
        [25],
        [-1]
    ])('should return existing entity', async(localStorageItemSize : number) => {
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

        configureTestBed();

        // act
        TestBed.runInInjectionContext(() => {
            resolver.resolve().subscribe(response => {
                // assert
                expect(response).toEqual(mockResponse);
                expect(splashScreenStateService.start).toBeCalled();
                expect(splashScreenStateService.stop).toBeCalled();
            });

            localStorage.removeItem('keystore_pageSize');
        });
    });
});