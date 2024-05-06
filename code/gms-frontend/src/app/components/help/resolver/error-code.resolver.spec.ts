import { HttpErrorResponse } from "@angular/common/http";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { of, throwError } from "rxjs";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { ErrorCode } from "../model/error-code.model";
import { ErrorCodeService } from "../service/error-code.service";
import { ErrorCodeResolver } from "./error-code.resolver";

/**
 * @author Peter Szrnka
 */
describe('ErrorCodeResolver', () => {
    let resolver: ErrorCodeResolver;
    let activatedRouteSnapshot: any;
    let splashScreenStateService: any;
    let service: any;

    const mockResponse: ErrorCode[] = [
        { code: "GMS-001", description: "Test 1" }
    ];

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            // add this to imports array
            imports: [HttpClientTestingModule],
            providers: [
                ErrorCodeResolver,
                { provide: SplashScreenStateService, useValue: splashScreenStateService },
                { provide: ErrorCodeService, useValue: service }
            ]
        }).compileComponents();

        resolver = TestBed.inject(ErrorCodeResolver);
    };

    beforeEach(async () => {
        splashScreenStateService = {
            start: jest.fn(),
            stop: jest.fn()
        };

        service = {
            list: jest.fn().mockReturnValue(of([]))
        };
    })

    it('should create', () => {
        configureTestBed();

        // assert
        expect(resolver).toBeTruthy();
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


        // act
        resolver.resolve(activatedRouteSnapshot).subscribe(response => {
            // assert
            expect(response).toEqual(mockResponse);
            expect(splashScreenStateService.start).toHaveBeenCalled();
            expect(splashScreenStateService.stop).toHaveBeenCalled();
        });
    });

    it('should handle error', async () => {
        activatedRouteSnapshot = {
            "params": {
                "id": "1"
            },
            "queryParams": {}
        };

        service = {
            list: jest.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error: new Error("!"), status: 500, statusText: "Oops!" })))
        };

        configureTestBed();

        // act
        resolver.resolve(activatedRouteSnapshot).subscribe(response => {
            // assert
            expect(response).toEqual(mockResponse);
            expect(splashScreenStateService.start).toHaveBeenCalled();
            expect(splashScreenStateService.stop).toHaveBeenCalled();
        });
    });
});