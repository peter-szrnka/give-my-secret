import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from "@angular/router";
import { catchError, combineLatest, map, Observable, throwError } from "rxjs";
import { Announcement } from "../model/announcement.model";
import { HomeData } from "../model/home-data.model";
import { User } from "../model/user.model";
import { AnnouncementService } from "../service/announcement-service";
import { ApiKeyService } from "../../components/apikey/service/apikey-service";
import { EventService } from "../../components/event/service/event-service";
import { KeystoreService } from "../service/keystore-service";
import { SharedDataService } from "../service/shared-data-service";
import { SplashScreenStateService } from "../service/splash-screen-service";
import { UserService } from "../service/user-service";
import { isSpecificUser } from "../utils/permission-utils";

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
            catchError(() => this.handleError([ [] as Event[], 0, 0 ])),
            map(([latestEvents, userCount]) => {
                return {
                    apiKeyCount: 0,
                    keystoreCount: 0,
                    userCount: userCount,
                    announcements: [],
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
            catchError(() => this.handleError([ [] as Announcement[], 0, 0 ])),
            map(([announcements, apiKeyCount, keystoreCount]) => {
                return {
                    apiKeyCount: apiKeyCount,
                    keystoreCount: keystoreCount,
                    userCount: 0,
                    announcements: announcements,
                    latestEvents: [],
                    isAdmin: isAdmin
                } as HomeData;
            })
        );
    }

    private handleError(data : any) {
        return this.sharedData.clearDataAndReturn(data);
    }
}