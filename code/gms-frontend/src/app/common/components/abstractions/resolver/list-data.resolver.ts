import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from "@angular/router";
import { catchError } from "rxjs";
import { Observable } from "rxjs/internal/Observable";
import { BaseList } from "../../../model/base-list";
import { SharedDataService } from "../../../service/shared-data-service";
import { SplashScreenStateService } from "../../../service/splash-screen-service";
import { ServiceBase } from "../service/service-base";
import { PageConfig } from "../../../model/common.model";

/**
 * @author Peter Szrnka
 */
export abstract class ListResolver<T, L extends BaseList<T>, S extends ServiceBase<T, L>> implements Resolve<any> {

    constructor(protected sharedData : SharedDataService, protected splashScreenStateService: SplashScreenStateService, protected service : S) {
    }

    abstract getPageConfig() : PageConfig;

    abstract getOrderProperty() : string;

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    public resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<BaseList<T>> {
        this.splashScreenStateService.start();

        return this.service.list({
            direction: "DESC",
            property : this.getOrderProperty(),
            page : route.queryParams['page'] || 0,
            size: JSON.parse(localStorage.getItem(this.getPageConfig().scope + "_pageSize") || '25')
        }).pipe(catchError(() => this.sharedData.clearDataAndReturn([])), (data) => {
            this.splashScreenStateService.stop();
            return data;
        });
    }
}