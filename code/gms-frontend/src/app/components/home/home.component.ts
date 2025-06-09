import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { Router, RouterModule } from "@angular/router";
import { firstValueFrom, takeUntil } from "rxjs";
import systemAnnouncements from "../../../assets/caas/system-announcements.json";
import { AngularMaterialModule } from "../../angular-material-module";
import { BaseComponent } from "../../common/components/abstractions/component/base.component";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { SharedDataService } from "../../common/service/shared-data-service";
import { Event } from "../event/model/event.model";
import { User } from "../user/model/user.model";
import { EMPTY_HOME_DATA, HomeData } from "./model/home-data.model";
import { SystemAnnouncement } from "./model/system-announcement.model";
import { HomeService } from "./service/home.service";

export enum PageStatus {
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
    styleUrls: ['./home.component.scss'],
    imports: [
        AngularMaterialModule,
        RouterModule,
        MomentPipe,
        InformationMessageComponent,
        TranslatorModule
    ]
})
export class HomeComponent extends BaseComponent implements OnInit {

    eventColumns: string[] = ['id', 'userId', 'eventDate', 'operation', 'target'];
    eventDataSource: ArrayDataSource<Event>;
    systemAnnouncementsData: SystemAnnouncement[] = systemAnnouncements;
    data: HomeData;
    pageStatus: PageStatus;
    editEnabled: boolean = false;
    error?: string;

    constructor(
        public router: Router,
        private readonly sharedData: SharedDataService,
        private readonly homeService: HomeService
    ) {
        super();
    }

    ngOnInit(): void {
        this.pageStatus = PageStatus.LOADING;
        firstValueFrom(this.homeService.getData())
            .then((homeData: HomeData) => {
                this.data = {
                    ...EMPTY_HOME_DATA,
                    ...this.data,
                    ...homeData
                };

                this.eventDataSource = new ArrayDataSource<Event>(this.data.events.resultList);
                this.pageStatus = PageStatus.LOADED;
            })
            .catch((err: Error) => {
                this.error = err.message;
                this.pageStatus = PageStatus.ERROR;
                this.eventDataSource = new ArrayDataSource<Event>([]);
            });
        this.sharedData.userSubject$
            .pipe(takeUntil(this.destroy$))
            .subscribe((user: User | undefined) => {
                this.data = {
                    ...EMPTY_HOME_DATA,
                    ...this.data
                };

                this.data.role = user?.role;
            });
        this.sharedData.authModeSubject$.pipe(takeUntil(this.destroy$)).subscribe(authMode => this.editEnabled = authMode === 'db');
    }
}