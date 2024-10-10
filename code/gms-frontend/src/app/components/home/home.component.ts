import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnDestroy, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { Subscription } from "rxjs";
import systemAnnouncements from "../../../assets/caas/system-announcements.json";
import { SharedDataService } from "../../common/service/shared-data-service";
import { Event } from "../event/model/event.model";
import { User } from "../user/model/user.model";
import { EMPTY_HOME_DATA, HomeData } from "./model/home-data.model";
import { SystemAnnouncement } from "./model/system-announcement.model";
import { HomeService } from "./service/home.service";

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
        private readonly sharedData: SharedDataService,
        private readonly homeService: HomeService,
    ) {
    }

    ngOnDestroy(): void {
        this.userSubscription.unsubscribe();
        this.authModeSubcription.unsubscribe();
        this.homeDataSubscription.unsubscribe();
    }

    ngOnInit(): void {
        this.pageStatus = PageStatus.LOADING;
        this.homeDataSubscription = this.homeService.getData()
            .subscribe((homeData: HomeData) => {
                this.data = {
                    ...EMPTY_HOME_DATA,
                    ...this.data,
                    ...homeData
                };

                this.eventDataSource = new ArrayDataSource<Event>(this.data.events.resultList);
                this.pageStatus = PageStatus.LOADED;
            });
        this.userSubscription = this.sharedData.userSubject$
            .subscribe((user: User | undefined) => {
                this.data = {
                    ...EMPTY_HOME_DATA,
                    ...this.data
                };

                this.data.role = user?.role;
            });
        this.authModeSubcription = this.sharedData.authModeSubject$.subscribe(authMode => this.editEnabled = authMode === 'db');
    }
}