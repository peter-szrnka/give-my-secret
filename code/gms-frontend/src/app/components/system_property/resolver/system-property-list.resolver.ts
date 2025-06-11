import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot } from "@angular/router";
import { Observable, catchError, of } from "rxjs";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { SystemPropertyList } from "../model/system-property-list.model";
import { SystemPropertyService } from "../service/system-property.service";

const EMPTY_SYSTEM_PROPERTY_LIST: SystemPropertyList = {
    resultList: [],
    totalElements: 0
};

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root'})
export class SystemPropertyListResolver {

    constructor(protected sharedData : SharedDataService, protected splashScreenStateService: SplashScreenStateService, protected service : SystemPropertyService) {
    }

    public resolve(activatedRouteSnapshot : ActivatedRouteSnapshot): Observable<SystemPropertyList> {
        this.splashScreenStateService.start();

        return this.service.list({
            direction: "DESC",
            property : "key",
            page : activatedRouteSnapshot.queryParams['page'] ?? 0,
            size: JSON.parse(localStorage.getItem("system_property_pageSize") ?? '25')
        }).pipe(catchError(() => of(EMPTY_SYSTEM_PROPERTY_LIST)), (data) => {
            this.splashScreenStateService.stop();
            return data;
        });
    }
}