import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from "@angular/router";
import { catchError } from "rxjs";
import { Observable } from "rxjs/internal/Observable";
import { BaseList } from "../model/base-list";
import { ServiceBase } from "../service/service-base";
import { SharedDataService } from "../service/shared-data-service";
import { SplashScreenStateService } from "../service/splash-screen-service";

export abstract class ListResolver<T, L extends BaseList<T>, S extends ServiceBase<T, L>> implements Resolve<any> {

    public tableConfig = {
        pageSize : 20
    };

    constructor(protected sharedData : SharedDataService, protected splashScreenStateService: SplashScreenStateService, protected service : S) {
    }

    abstract getOrderProperty() : string;

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    public resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<T[]> {
        this.splashScreenStateService.start();

        return this.service.list({
            direction: "DESC",
            property : this.getOrderProperty(),
            page : 0,
            size: this.tableConfig.pageSize
        }).pipe(catchError(() => this.sharedData.clearDataAndReturn([])), (data) => {
            this.splashScreenStateService.stop();
            return data;
        });
    }
}