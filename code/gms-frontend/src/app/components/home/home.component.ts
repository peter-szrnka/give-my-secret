import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnDestroy, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { SharedDataService } from "../../common/service/shared-data-service";
import { Event } from "../event/model/event.model";
import { User } from "../user/model/user.model";
import { EMPTY_HOME_DATA, HomeData } from "./model/home-data.model";
import { Observable, ReplaySubject, Subscription, catchError, map, mergeMap, of } from "rxjs";
import { HomeService } from "./service/home.service";
import systemAnnouncements from "../../../assets/caas/system-announcements.json";
import { SystemAnnouncement } from "./model/system-announcement.model";

enum PageStatus {
    LOADING = 0,
    LOADED = 1,
    ERROR = 2
};

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {

    eventColumns: string[] = ['id', 'userId', 'eventDate', 'operation', 'target'];
    eventDataSource: ArrayDataSource<Event>;
    systemAnnouncementsData: SystemAnnouncement[] = systemAnnouncements;
    data: HomeData;
    pageStatus: PageStatus;
    editEnabled: boolean = false;
    error?: string;

    userSubscription: Subscription;
    authModeSubcription: Subscription;
    homeDataSubscription: Subscription;

    constructor(
        public router: Router,
        private sharedData: SharedDataService,
        private homeService: HomeService,
    ) {
    }
    ngOnDestroy(): void {
        this.userSubscription.unsubscribe();
        this.authModeSubcription.unsubscribe();
        this.homeDataSubscription.unsubscribe();
    }

    ngOnInit(): void {
        this.pageStatus = PageStatus.LOADING;
        this.homeDataSubscription = this.homeService.getData().subscribe((homeData: HomeData) => {

            this.data = {
                ...EMPTY_HOME_DATA,
                ...this.data,
                ...homeData
            };
        });
        this.userSubscription = this.sharedData.userSubject$
            .subscribe((user: User | undefined) => {
                this.data = {
                    ...EMPTY_HOME_DATA,
                    ...this.data
                };

                this.data.role = user?.roles[0];

                this.eventDataSource = new ArrayDataSource<Event>(this.data.events.resultList);
                this.pageStatus = PageStatus.LOADED;
            });
        /*this.userSubscription = this.sharedData.userSubject$
            .pipe(mergeMap((user: User | undefined): Observable<HomeData> => this.processUser(user)))
            .pipe(catchError((err) => { 
                this.error = err.error.message;
                this.pageStatus = PageStatus.ERROR;
                return of({ ...EMPTY_HOME_DATA });
            }))
            .subscribe((homeData: HomeData) => {
                this.data = {
                    ...EMPTY_HOME_DATA,
                    ...homeData
                };
                this.eventDataSource = new ArrayDataSource<Event>(this.data.events.resultList);
                this.pageStatus = PageStatus.LOADED;
            });*/
        this.authModeSubcription = this.sharedData.authModeSubject$.subscribe(authMode => this.editEnabled = authMode === 'db');
    }

    /*private processUser(user: User | undefined): Observable<HomeData> {
        if (!user) {
            return of(EMPTY_HOME_DATA);
        }

        return this.homeService.getData().pipe(map((response): HomeData => {
            const data: HomeData = response;
            // TODO Refactor the app to allow only 1 type of role
            data.role = user.roles[0];
            return data;
        }));
    }*/
}