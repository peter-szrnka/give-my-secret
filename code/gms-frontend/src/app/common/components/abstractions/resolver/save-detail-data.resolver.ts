import { ActivatedRouteSnapshot } from "@angular/router";
import { catchError, of } from "rxjs";
import { Observable } from "rxjs/internal/Observable";
import { BaseList } from "../../../model/base-list";
import { SharedDataService } from "../../../service/shared-data-service";
import { SplashScreenStateService } from "../../../service/splash-screen-service";
import { ServiceBase } from "../service/service-base";

/**
 * @author Peter Szrnka
 */
export abstract class DetailDataResolver<T, S extends ServiceBase<T, BaseList<T>>> {

    constructor(protected sharedData : SharedDataService, protected splashScreenStateService: SplashScreenStateService, protected service : S) {
    }

    protected abstract getEmptyResponse() : T;

    public resolve(route: ActivatedRouteSnapshot): Observable<T> {
        if(route.params['id'] === 'new') {
            return of(this.getEmptyResponse());
        }

        this.splashScreenStateService.start();

        return this.service.getById(route.params['id'])
        .pipe(catchError((err) => of({ entity: {}, error: err.error.message }) as Observable<any>));
    }
}