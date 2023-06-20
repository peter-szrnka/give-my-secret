import { Injectable, inject } from "@angular/core";
import { Observable, catchError } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { SystemProperty } from "../model/system-property.model";
import { SystemPropertyService } from "../service/system-property.service";
import { ActivatedRouteSnapshot } from "@angular/router";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class SystemPropertyListResolver {

    constructor(protected sharedData : SharedDataService, protected splashScreenStateService: SplashScreenStateService, protected service : SystemPropertyService) {
    }

    public resolve(): Observable<SystemProperty[]> {
        this.splashScreenStateService.start();

        return this.service.list({
            direction: "DESC",
            property : "key",
            page : inject(ActivatedRouteSnapshot).queryParams['page'] ?? 0,
            size: JSON.parse(localStorage.getItem("system_property_pageSize") ?? '25')
        }).pipe(catchError(() => this.sharedData.clearDataAndReturn([])), (data) => {
            this.splashScreenStateService.stop();
            return data;
        });
    }
}