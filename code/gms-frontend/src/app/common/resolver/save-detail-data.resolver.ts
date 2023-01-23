import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from "@angular/router";
import { catchError, of } from "rxjs";
import { Observable } from "rxjs/internal/Observable";
import { BaseList } from "../model/base-list";
import { ServiceBase } from "../service/service-base";
import { SharedDataService } from "../service/shared-data-service";
import { SplashScreenStateService } from "../service/splash-screen-service";

/**
 * @author Peter Szrnka
 */
export abstract class DetailDataResolver<T, S extends ServiceBase<T, BaseList<T>>> implements Resolve<any> {

    constructor(protected sharedData : SharedDataService, protected splashScreenStateService: SplashScreenStateService, protected service : S) {
    }

    protected abstract getEmptyResponse() : T;

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    public resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<T> {
        if(route.params['id'] === 'new') {
            return of(this.getEmptyResponse());
        }

        this.splashScreenStateService.start();

        return this.service.getById(route.params['id'])
        .pipe(catchError(() => this.sharedData.clearDataAndReturn({} as T)), (data) => {
            this.splashScreenStateService.stop();
            return data;
        });
    }
}