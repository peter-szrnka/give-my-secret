import { HttpErrorResponse, HttpRequest, HttpResponse } from "@angular/common/http";
import { of, throwError } from "rxjs";
import { MockInterceptor } from "./mock-interceptor";

/**
 * @author Peter Szrnka
 */
describe('MockInterceptor', () => {
    let interceptor: MockInterceptor;
    let handler : any;
    const sampleUrl = 'https://www.sample.com/';

    beforeEach(() => {
        handler = {
            handle: () => {
                const event : HttpResponse<any> = new HttpResponse({ body : 'OK', status : 200 });
                return of(event);
            }
        };

        interceptor = new MockInterceptor();
    });

  afterEach(() => { localStorage.clear(); });

    it('should proceed', () => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'authenticate', {});

        // act
        interceptor.intercept(req, handler).subscribe();

        // assert
    });
});