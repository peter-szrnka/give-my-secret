import { HttpErrorResponse, HttpRequest, HttpResponse } from "@angular/common/http";
import { firstValueFrom, of, throwError } from "rxjs";
import { AuthInterceptor } from "./auth-interceptor";
import { vi } from "vitest";

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
            clearData : vi.fn(),
            logout : vi.fn()
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

    it('should proceed', async() => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'authenticate', {});

        // act
        const response = await firstValueFrom(interceptor.intercept(req, handler));

        // assert
        expect(response).toBeDefined();
        expect(sharedData.clearData).toHaveBeenCalledTimes(0);
    });

    it('should not proceed because of 0', async() => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'get_data', {});
        handler = createHandler("Connection refused", 0);

        // act
        const response = await firstValueFrom(interceptor.intercept(req, handler));

        // assert
        expect(sharedData.clearData).toHaveBeenCalled();
    });

    it('should not proceed because of 401', async() => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'get_data', {});
        handler = createHandler("Authentication failed", 401);

        // act
        const response = await firstValueFrom(interceptor.intercept(req, handler));

        // assert
        expect(sharedData.clearData).toHaveBeenCalled();
    });

    it('should not proceed because of 403', async() => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'get_data', {});
        handler = createHandler("Authentication failed", 403);

        // act
        const response = await firstValueFrom(interceptor.intercept(req, handler));

        // assert
        expect(sharedData.clearData).toHaveBeenCalled();
    });

    it('should not proceed because of 500', async() => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'get_data', {});
        handler = createHandler("Internal server error", 500);

        // act
        try {
            const response = await firstValueFrom(interceptor.intercept(req, handler));
        } catch(e: any) {
            expect(e.message).toEqual("Http failure response for (unknown url): 500 undefined");
        }

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