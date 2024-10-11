import { HttpRequest, HttpResponse } from '@angular/common/http';
import { firstValueFrom, of } from 'rxjs';
import { MockInterceptor } from './mock-empty-interceptor';

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

    it('should return normal response', async () => {
        const req : HttpRequest<any> = new HttpRequest('GET', sampleUrl + 'info/me', {});

        // act
        const response: HttpResponse<any> = await firstValueFrom(interceptor.intercept(req, handler)) as HttpResponse<any>;
  
        // assert
        expect(response).toBeDefined();
        expect(response.status).toStrictEqual(200);
    });
});