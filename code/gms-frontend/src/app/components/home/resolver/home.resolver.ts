import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from "@angular/router";
import { catchError, combineLatest, map, Observable, throwError } from "rxjs";
import { HomeData } from "../model/home-data.model";
import { User } from "../../user/model/user.model";
import { AnnouncementService } from "../../announcement/service/announcement-service";
import { ApiKeyService } from "../../apikey/service/apikey-service";
import { EventService } from "../../event/service/event-service";
import { KeystoreService } from "../../keystore/service/keystore-service";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { UserService } from "../../user/service/user-service";
import { isSpecificUser } from "../../../common/utils/permission-utils";
import { AnnouncementList } from "../../announcement/model/annoucement-list.model";
import { EventList } from "../../event/model/event-list.model";

const EVENT_LIST_FILTER = {
    direction: "DESC",
    property: "eventDate",
    page: 0,
    size: 10
};

const ANNOUNCEMENT_LIST_FILTER = {
    direction: "DESC",
    property: "announcementDate",
    page: 0,
    size: 10
};

const NO_ANNOUNCEMENTS = { resultList : [], totalElements : 0 } as AnnouncementList;
const NO_EVENTS = { resultList : [], totalElements : 0 } as EventList;

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn : 'root' })
export class HomeResolver implements Resolve<HomeData> {

    constructor(
        private sharedData: SharedDataService,
        private eventService: EventService,
        private userService: UserService,
        private annoucementService: AnnouncementService,
        private apiKeyService: ApiKeyService,
        private keystoreService: KeystoreService,
        private splashScreenStateService: SplashScreenStateService
    ) { }

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    public resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<HomeData> {
        const user: User | undefined = this.sharedData.getUserInfo();

        if (user === undefined) {
            return throwError(() => "Invalid user");
        }

        const isAdmin = isSpecificUser(user.roles, 'ROLE_ADMIN');
        this.splashScreenStateService.start();

        return isAdmin ? this.getAdminData(isAdmin) : this.getUserData(isAdmin);
    }

    private getAdminData(isAdmin: boolean): Observable<HomeData> {
        return combineLatest([
            this.eventService.list(EVENT_LIST_FILTER),
            this.userService.count()
        ]).pipe(
            catchError(() => this.handleError([ NO_EVENTS, 0, 0 ])),
            map(([latestEvents, userCount]) => {
                return {
                    apiKeyCount: 0,
                    keystoreCount: 0,
                    userCount: userCount,
                    announcements: NO_ANNOUNCEMENTS,
                    latestEvents: latestEvents,
                    isAdmin: isAdmin
                } as HomeData;
            }),
        );
    }

    private getUserData(isAdmin: boolean): Observable<HomeData> {
        return combineLatest([
            this.annoucementService.list(ANNOUNCEMENT_LIST_FILTER),
            this.apiKeyService.count(),
            this.keystoreService.count()
        ]).pipe(
            catchError(() => this.handleError([ NO_ANNOUNCEMENTS, 0, 0 ])),
            map(([announcements, apiKeyCount, keystoreCount]) => {
                return {
                    apiKeyCount: apiKeyCount,
                    keystoreCount: keystoreCount,
                    userCount: 0,
                    announcements: announcements,
                    latestEvents: NO_EVENTS,
                    isAdmin: isAdmin
                } as HomeData;
            })
        );
    }

    private handleError(data : any) {
        return this.sharedData.clearDataAndReturn(data);
    }
}