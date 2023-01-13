/* eslint-disable @typescript-eslint/no-explicit-any */
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, EMPTY, map, Observable, of, throwError } from "rxjs";
import { SharedDataService } from "../service/shared-data-service";
import { AuthService } from "../service/auth-service";

type NewType = Observable<any>;

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    
    constructor(private sharedData : SharedDataService, private authService : AuthService) { }

    private handleAuthError(err: HttpErrorResponse): NewType {
        /*if (err.status === 403 || err.status === 406) {
            console.info("Let's try to refresh the token!!!");
            this.authService.refreshToken();
            return of();
        } else */if (err.status >= 300 && err.status < 500) {
            this.sharedData.logout();
            return of(err.message);
        }

        return throwError(() => err);
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        if (req.url.lastIndexOf("/authenticate") !== -1) {
            return next.handle(req);
        }

        return next.handle(req).pipe(catchError(x=> this.handleAuthError(x)));
    }
}