import { HttpErrorResponse, HttpRequest, HttpResponse } from "@angular/common/http";
import { of, throwError } from "rxjs";
import { AuthInterceptor } from "./auth-interceptor";

describe('AuthInterceptor', () => {
    let interceptor: AuthInterceptor;
    let sharedData : any;
    let handler : any;
    const sampleUrl = 'https://www.sample.com/';

    beforeEach(() => {
        sharedData = {
            logout : jest.fn()
        };

        handler = {
            handle: () => {
                const event : HttpResponse<any> = new HttpResponse({ body : 'OK', status : 200 });
                return of(event);
            }
        };

        interceptor = new AuthInterceptor(sharedData);
    });

  afterEach(() => { localStorage.clear(); });

    it('should proceed', () => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'authenticate', {});

        // act
        interceptor.intercept(req, handler).subscribe();

        // assert
        expect(sharedData.logout).toBeCalledTimes(0);
    });

    it('should not proceed because of 401', () => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'get_data', {});

        handler = {
            handle: () => throwError(() => new HttpErrorResponse({
                error: new Error("Authentication failed"),
                status: 401
            }))
        } as any;

        // act
        interceptor.intercept(req, handler).subscribe();

        // assert
        expect(sharedData.logout).toBeCalledTimes(1);
    });

    it('should not proceed because of 403', () => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'get_data', {});

        handler = {
            handle: () => throwError(() => new HttpErrorResponse({
                error: new Error("Authentication failed"),
                status: 403
            }))
        } as any;

        // act
        interceptor.intercept(req, handler).subscribe();

        // assert
        expect(sharedData.logout).toBeCalledTimes(1);
    });

    it('should not proceed because of 500', () => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'get_data', {});

        handler = {
            handle: () => throwError(new HttpErrorResponse({
                error: new Error("Internal server error"),
                status: 500
            }))
        } as any;

        // act
        interceptor.intercept(req, handler).subscribe();

        // assert
        expect(sharedData.logout).toBeCalledTimes(0);
    });
});