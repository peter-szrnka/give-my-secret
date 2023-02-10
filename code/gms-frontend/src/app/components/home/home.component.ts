import { ArrayDataSource } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { HomeData } from "./model/home-data.model";
import { Event } from "../event/model/event.model";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";

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
        protected activatedRoute: ActivatedRoute,
        private splashScreenService: SplashScreenStateService
    ) {
    }

    ngOnInit(): void {
        this.activatedRoute.data
            .subscribe((response: any) => {
                this.data = response['data'];
                this.eventDataSource = new ArrayDataSource<Event>(this.data.latestEvents);
                this.splashScreenService.stop();
            });
    }
}