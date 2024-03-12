import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { SharedDataService } from "../../common/service/shared-data-service";
import { Event } from "../event/model/event.model";
import { User } from "../user/model/user.model";
import { EMPTY_HOME_DATA, HomeData } from "./model/home-data.model";
import { Observable, map, mergeMap, of } from "rxjs";
import { HomeService } from "./service/home.service";
import systemAnnouncements from "../../../assets/caas/system-announcements.json";
import { SystemAnnouncement } from "./model/system-announcement.model";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

    eventColumns: string[] = ['id', 'userId', 'eventDate', 'operation', 'target'];
    eventDataSource: ArrayDataSource<Event>;
    systemAnnouncementsData: SystemAnnouncement[] = systemAnnouncements;
    data: HomeData;
    loading: string = '';
    editEnabled: boolean = false;

    constructor(
        public router: Router,
        private sharedData: SharedDataService,
        private homeService: HomeService,
    ) {}

    ngOnInit(): void {
        console.info(this.systemAnnouncementsData);
        this.loading = 'LOADING';
        this.sharedData.userSubject$
            .pipe(mergeMap((user: User | undefined): Observable<HomeData> => this.processUser(user)))
            .subscribe((homeData: HomeData) => {
                this.data = {
                    ...EMPTY_HOME_DATA,
                    ...homeData
                };
                this.eventDataSource = new ArrayDataSource<Event>(this.data.events.resultList);
                this.loading = 'LOADED';
            });
        this.sharedData.authModeSubject$.subscribe(authMode => this.editEnabled = authMode === 'db');
    }

    private processUser(user: User | undefined): Observable<HomeData> {
        if (!user) {
            return of(EMPTY_HOME_DATA);
        }

        return this.homeService.getData().pipe(map((response): HomeData => {
            const data: HomeData = response;
            // TODO Refactor the app to allow only 1 type of role
            data.role = user.roles[0];
            return data;
        }));
    }
}