import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { SharedDataService } from "../../common/service/shared-data-service";
import { Event } from "../event/model/event.model";
import { User } from "../user/model/user.model";
import { EMPTY_HOME_DATA, HomeData } from "./model/home-data.model";
import { Observable, map, mergeMap, of } from "rxjs";
import { HomeService } from "./service/home.service";

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
    data: HomeData;
    loading: string = '';

    constructor(
        public router: Router,
        private sharedData: SharedDataService,
        private homeService: HomeService,
    ) {}

    ngOnInit(): void {
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
    }

    private processUser(user: User | undefined): Observable<HomeData> {
        if (!user) {
            return of(EMPTY_HOME_DATA);
        }

        return this.homeService.getData().pipe(map((response): HomeData => {
            const data: HomeData = response;
            // TODO Refactor the app to allow only 1 type of role
            data.role = user.roles?.[0];
            return data;
        }));
    }
}