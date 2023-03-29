import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from "@angular/router";
import { catchError, map, Observable, throwError } from "rxjs";
import { HomeData } from "../model/home-data.model";
import { User } from "../../user/model/user.model";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { isSpecificUser } from "../../../common/utils/permission-utils";
import { HomeService } from "../service/home.service";

export const EMPTY_HOME_ADMIN_DATA: HomeData = {
    announcementCount: 0,
    userCount: 0,
    events: { resultList: [], totalElements: 0 },
    admin: true,
    apiKeyCount: 0,
    keystoreCount: 0,
    secretCount: 0,
    announcements: { resultList: [], totalElements: 0 }
};

export const EMPTY_HOME_USER_DATA: HomeData = {
    announcements: { resultList: [], totalElements: 0 },
    apiKeyCount: 0,
    keystoreCount: 0,
    secretCount: 0,
    admin: false,
    announcementCount: 0,
    userCount: 0,
    events: { resultList: [], totalElements: 0 }
};

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class HomeResolver implements Resolve<HomeData> {

    constructor(
        private sharedData: SharedDataService,
        private homeService: HomeService,
        private splashScreenStateService: SplashScreenStateService
    ) { }

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    public resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<HomeData> {
        const user: User | undefined = this.sharedData.getUserInfo();

        if (user === undefined) {
            return throwError(() => "Invalid user");
        }

        this.splashScreenStateService.start();
        return this.getData(isSpecificUser(user.roles, 'ROLE_ADMIN'));
    }

    private getData(isAdmin: boolean): Observable<HomeData> {
        return this.homeService.getData().pipe(
            catchError(() => this.handleError(isAdmin)),
            map(homeData => {
                homeData.admin = isAdmin;
                return homeData;
            })
        );
    }

    private handleError(isAdmin: boolean) {
        return this.sharedData.clearDataAndReturn(isAdmin ? EMPTY_HOME_ADMIN_DATA : EMPTY_HOME_USER_DATA);
    }
}