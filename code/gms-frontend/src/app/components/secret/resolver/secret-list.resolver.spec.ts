import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ActivatedRoute } from "@angular/router";
import { of } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { Secret } from "../model/secret.model";
import { SecretService } from "../service/secret-service";
import { SecretListResolver } from "./secret-list.resolver";

/**
 * @author Peter Szrnka
 */
describe('SecretListResolver', () => {
    let resolver : SecretListResolver;
    let activatedRouteSnapshot : any;
    let splashScreenStateService : any;
    let service : any;
    let sharedData : any;

    const mockResponse : Secret[] = [{
        id : 1,
        status : "ACTIVE",
        rotationPeriod : "HOURLY",
        value: "value-1",
        apiKeyRestrictions : [],
        type : 'CREDENTIAL'
    }];

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            // add this to imports array
            imports: [HttpClientTestingModule],
            providers: [
              SecretListResolver,
              { provide : ActivatedRoute, useValue : { 'snapshot': activatedRouteSnapshot} },
              { provide: SplashScreenStateService, useValue : splashScreenStateService },
              { provide : SecretService, useValue : service },
              { provide : SharedDataService, useValue: sharedData }
          ]
          }).compileComponents();
          resolver = TestBed.inject(SecretListResolver);
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
        const route : any = jest.fn();
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