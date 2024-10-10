import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot } from "@angular/router";
import { Observable, catchError, of } from "rxjs";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { ErrorCodeList } from "../model/error-code-list.model";
import { ErrorCodeService } from "../service/error-code.service";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class ErrorCodeResolver {

    constructor(
        private readonly splashScreenStateService: SplashScreenStateService, 
        private readonly service: ErrorCodeService) {
    }

    public resolve(snapshot: ActivatedRouteSnapshot): Observable<ErrorCodeList> {
        this.splashScreenStateService.start();

        return this.service.list()
            .pipe(catchError(() =>  of({ errorCodeList: [] }) as Observable<any>));
    }
}