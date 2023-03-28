import { Injectable } from "@angular/core";
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { Observable, catchError } from "rxjs";
import { SystemProperty } from "../model/system-property.model";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { SystemPropertyService } from "../service/system-property.service";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class SystemPropertyListResolver implements Resolve<any> {

    constructor(protected sharedData : SharedDataService, protected splashScreenStateService: SplashScreenStateService, protected service : SystemPropertyService) {
    }

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    public resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<SystemProperty[]> {
        this.splashScreenStateService.start();

        return this.service.list({
            direction: "DESC",
            property : "key",
            page : 0,
            size: JSON.parse(localStorage.getItem("system_property_pageSize") || '25')
            //this.tableConfig.pageSize
        }).pipe(catchError(() => this.sharedData.clearDataAndReturn([])), (data) => {
            this.splashScreenStateService.stop();
            return data;
        });
    }
}