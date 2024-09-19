import { Component, Input, OnDestroy, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { map, Observable, Subscription, takeWhile, timer } from "rxjs";
import { SharedDataService } from "../../service/shared-data-service";
import { InfoDialog } from "../info-dialog/info-dialog.component";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'automatic-logout',
    templateUrl: './automatic-logout.component.html',
    styleUrls: ['./automatic-logout.component.scss']
})
export class AutomaticLogoutComponent implements OnInit, OnDestroy {

    @Input() automaticLogoutTimeInMinutes: number;
    timeLeft: Observable<number>;
    timeLeftSubscription: Subscription;
    logoutComing: boolean = false;

    constructor(private sharedData: SharedDataService, private dialog: MatDialog) { }

    ngOnInit(): void {
        this.sharedData.resetTimerSubject$.subscribe(() => {
            this.timeLeftSubscription?.unsubscribe();
            this.initiateTimer();
        });
    }

    ngOnDestroy(): void {
        this.timeLeftSubscription?.unsubscribe();
    }

    initiateTimer(): void {
        this.timeLeft = timer(0, 1000).pipe(
            map(n => (this.automaticLogoutTimeInMinutes*1000*60) - (n * 1000)),
            takeWhile(n => n >= 0) 
        );

        this.timeLeftSubscription = this.timeLeft.subscribe(n => {
            this.logoutComing = (n <= 30000);
            
            if (n === 0) {
                this.dialog.open(InfoDialog, { data: { title: 'Automatic Logout', text: 'You have been logged out due to inactivity.', type: 'information' } });
                this.sharedData.logout();
            }
        });
    }
}