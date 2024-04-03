import { HttpErrorResponse, HttpRequest, HttpResponse } from "@angular/common/http";
import { of, throwError } from "rxjs";
import { AuthInterceptor } from "./auth-interceptor";

/**
 * @author Peter Szrnka
 */
describe('AuthInterceptor', () => {
    let interceptor: AuthInterceptor;
    let sharedData : any;
    let handler : any;
    const sampleUrl = 'https://www.sample.com/';

    beforeEach(() => {
        sharedData = {
            clearData : jest.fn(),
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
        expect(sharedData.clearData).toHaveBeenCalledTimes(0);
    });

    it('should not proceed because of 0', () => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'get_data', {});
        handler = createHandler("Connection refused", 0);

        // act
        interceptor.intercept(req, handler).subscribe();

        // assert
        expect(sharedData.clearData).toHaveBeenCalled();
    });

    it('should not proceed because of 401', () => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'get_data', {});
        handler = createHandler("Authentication failed", 401);

        // act
        interceptor.intercept(req, handler).subscribe();

        // assert
        expect(sharedData.clearData).toHaveBeenCalled();
    });

    it('should not proceed because of 403', () => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'get_data', {});
        handler = createHandler("Authentication failed", 403);

        // act
        interceptor.intercept(req, handler).subscribe();

        // assert
        expect(sharedData.clearData).toHaveBeenCalled();
    });

    it('should not proceed because of 500', () => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'get_data', {});
        handler = createHandler("Internal server error", 500);

        // act
        interceptor.intercept(req, handler).subscribe();

        // assert
        expect(sharedData.clearData).toHaveBeenCalledTimes(0);
    });

    function createHandler(errorMessage : string, statusCode : number) : any {
        return {
            handle: () => throwError(() => new HttpErrorResponse({
                error: new Error(errorMessage),
                status: statusCode
            }))
        } as any;
    }
});