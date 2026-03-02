import { TestBed } from "@angular/core/testing";
import { firstValueFrom, of } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { Secret } from "../model/secret.model";
import { SecretService } from "../service/secret-service";
import { SecretListResolver } from "./secret-list.resolver";
import { vi } from "vitest";
import { HttpClientTestingModule } from "@angular/common/http/testing";

/**
 * @author Peter Szrnka
 */
describe('SecretListResolver', () => {
    let resolver: SecretListResolver;
    let activatedRouteSnapshot: any;
    let splashScreenStateService: any;
    let service: any;
    let sharedData: any;

    const mockResponse: Secret[] = [{
        id: 1,
        status: "ACTIVE",
        rotationPeriod: "HOURLY",
        value: "value-1",
        apiKeyRestrictions: [],
        type: 'CREDENTIAL'
    }];

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                SecretListResolver,
                { provide: SplashScreenStateService, useValue: splashScreenStateService },
                { provide: SecretService, useValue: service },
                { provide: SharedDataService, useValue: sharedData }
            ]
        }).compileComponents();
        resolver = TestBed.inject(SecretListResolver);
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
            "params": {
                "id": "1"
            },
            "queryParams": {
                "page": "0"
            }
        };
        configureTestBed();

        const response = firstValueFrom(resolver.resolve(activatedRouteSnapshot));

        // assert
        expect(splashScreenStateService.start).toHaveBeenCalled();
    });
});