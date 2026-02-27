import { HttpErrorResponse } from "@angular/common/http";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { firstValueFrom, of, throwError } from "rxjs";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { ErrorCodeList } from "../model/error-code-list.model";
import { ErrorCodeService } from "../service/error-code.service";
import { ErrorCodeResolver } from "./error-code.resolver";
import { vi } from "vitest";

/**
 * @author Peter Szrnka
 */
describe('ErrorCodeResolver', () => {
    let resolver: ErrorCodeResolver;
    let activatedRouteSnapshot: any;
    let splashScreenStateService: any;
    let service: any;

    const mockResponse: ErrorCodeList = {
        errorCodeList: [
            { code: "GMS-001", "description": "Unexpected IO error" },
            { code: "GMS-100", "description": "N/A" }
        ]
    };

    const configureTestBed = () => {
        TestBed.configureTestingModule({
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
            start: vi.fn(),
            stop: vi.fn()
        };

        service = {
            list: vi.fn().mockReturnValue(of([]))
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
        service = {
            list: vi.fn().mockReturnValue(of(mockResponse))
        };
        configureTestBed();


        // act
        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));

        // assert
        expect(response).toEqual(mockResponse);
        expect(splashScreenStateService.start).toHaveBeenCalled();
    });

    it('should handle error', async () => {
        activatedRouteSnapshot = {
            "params": {
                "id": "1"
            },
            "queryParams": {}
        };

        service = {
            list: vi.fn().mockReturnValue(throwError(() => new HttpErrorResponse({ error: new Error("!"), status: 500, statusText: "Oops!" })))
        };

        configureTestBed();

        // act
        const response = await firstValueFrom(resolver.resolve(activatedRouteSnapshot));

        // assert
        expect(response).toEqual({ errorCodeList: [] });
        expect(splashScreenStateService.start).toHaveBeenCalled();
    });
});