import { HttpRequest } from "@angular/common/http";
import { CsrfTokenInterceptor } from "./csrf-token-interceptor";
import { vi } from "vitest";

/**
 * @author Peter Szrnka
 */
describe('CsrfTokenInterceptor', () => {
    let interceptor: CsrfTokenInterceptor;
    let documentMock: Document;

    beforeEach(() => {
        documentMock = {
            cookie: 'XSRF-TOKEN=test-token; other-cookie=value',
        } as Document;

        interceptor = new CsrfTokenInterceptor(documentMock);
    });

    it('should return the unchanged request if no XSRF-TOKEN cookie is present', () => {
        documentMock.cookie = 'other-cookie=value'; // No XSRF-TOKEN cookie

        const req = { headers: { set: vi.fn() } } as any;
        const next = { handle: vi.fn().mockReturnValue({}) };

        interceptor.intercept(req, next);

        expect(req.headers.set).not.toHaveBeenCalled();
        expect(next.handle).toHaveBeenCalledWith(req);
    });

    it('should add the X-XSRF-TOKEN header if cookie is present', ()=> {
        const req: HttpRequest<any> = new HttpRequest<any>('GET', '/test');
        req.clone = vi.fn().mockReturnValue(req);
        const next = { handle: vi.fn().mockReturnValue({}) };

        interceptor.intercept(req, next);

        expect(req.clone).toHaveBeenCalledWith({
            setHeaders: {
                "X-XSRF-TOKEN": 'test-token'
            }
        });
        expect(next.handle).toHaveBeenCalledWith(req);
    });
});
