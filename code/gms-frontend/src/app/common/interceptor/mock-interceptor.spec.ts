import { HttpRequest, HttpResponse } from "@angular/common/http";
import { firstValueFrom, of } from "rxjs";
import { MockInterceptor } from "./mock-interceptor";

import * as infoMe from "../../mock/info.me.json";

/**
 * @author Peter Szrnka
 */
describe('MockInterceptor', () => {
    let interceptor: MockInterceptor;
    let handler : any;
    let env: any;
    let logger: any;
    const sampleUrl = 'https://www.sample.com/';

    beforeEach(() => {
        handler = {
            handle: () => {
                const event : HttpResponse<any> = new HttpResponse({ body : 'OK', status : 200 });
                return of(event);
            }
        };

         env = {
            enableMock: jest.fn()
         };

         logger = {
            info: jest.fn()
         };

        interceptor = new MockInterceptor(env, logger);
    });

    it('should return normal response when mocking disabled', async () => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'info/me', {});
        env.enableMock = false;

        // act
        const response: HttpResponse<any> = await firstValueFrom(interceptor.intercept(req, handler)) as HttpResponse<any>;
  
        // assert
        expect(response).toBeDefined();
        expect(response.status).toStrictEqual(200);
        expect(env.enableMock).toBeFalsy();
        expect(logger.info).toHaveBeenCalledTimes(0);
    });

    it('should mock response when mocking enabled', async () => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'info/me', {});
        env.enableMock = jest.fn().mockReturnValue(true);

        // act
        const response: HttpResponse<any> = await firstValueFrom(interceptor.intercept(req, handler)) as HttpResponse<any>;
  
        // assert
        expect(response).toBeDefined();
        expect(response.status).toStrictEqual(200);
        expect(response.body).toStrictEqual(infoMe);
        expect(env.enableMock).toBeTruthy();
        expect(logger.info).toHaveBeenCalled();
    });

    it('should return empty response when an unhandled url called', async () => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'unknown', {});
        env.enableMock = jest.fn().mockReturnValue(true);

        // act
        const response: HttpResponse<any> = await firstValueFrom(interceptor.intercept(req, handler)) as HttpResponse<any>;
  
        // assert
        expect(response).toBeDefined();
        expect(response.status).toStrictEqual(200);
        expect(response.body).toStrictEqual({});
        expect(env.enableMock).toBeTruthy();
        expect(logger.info).toHaveBeenCalled();
    });
});