import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { SharedDataService } from "../../common/service/shared-data-service";
import { isSpecificUser } from "../../common/utils/permission-utils";
import { Event } from "../event/model/event.model";
import { User } from "../user/model/user.model";
import { EMPTY_HOME_DATA, HomeData } from "./model/home-data.model";
import { Observable, map, mergeMap } from "rxjs";
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

    constructor(
        public router: Router,
        private sharedData: SharedDataService,
        private homeService: HomeService,
    ) {}

    ngOnInit(): void {
        this.sharedData.userSubject$
            .pipe(mergeMap((user: User | undefined): Observable<HomeData> => this.processUser(user)))
            .subscribe((homeData: HomeData) => {
                this.data = {
                    ...EMPTY_HOME_DATA,
                    ...homeData
                };
                this.eventDataSource = new ArrayDataSource<Event>(this.data.events.resultList);
            });
    }

    private processUser(user: User | undefined): Observable<HomeData> {
        if (!user) {
            throw new Error('Invalid user!');
        }

        return this.homeService.getData().pipe(map((response): HomeData => {
            const data: HomeData = response;
            data.admin = isSpecificUser(user.roles, 'ROLE_ADMIN');
            return data;
        }));
    }
}