/* eslint-disable @typescript-eslint/no-explicit-any */
import { DOCUMENT } from "@angular/common";
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class CsrfTokenInterceptor implements HttpInterceptor {

    constructor(@Inject(DOCUMENT) private readonly document: Document) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        req = this.addCsrfToken(req);
        return next.handle(req);
    }

    private addCsrfToken(req: HttpRequest<any>): HttpRequest<any> {
        const cookie = this.document.cookie.split(";").find(c => c.trim().startsWith("XSRF-TOKEN="));
            
        if (!cookie) {
            return req;
        }
                
        const csrfToken = cookie.split("=")[1];

        return req.clone({
            setHeaders: {
                "X-XSRF-TOKEN": csrfToken
            }
        });
    }
}