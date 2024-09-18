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

    @Input() automaticLogoutTimeInMs: number;
    @Input() warningBeforeLogoutInMs?: number = 30000;
    timerValue: number = 1000;
    timerStep: number = 1000;

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
        this.timeLeftSubscription.unsubscribe();
    }

    private initiateTimer(): void {
        console.info("initiate timer: timerValue = " + this.timerValue + ", automaticLogoutTimeInMs = " + this.automaticLogoutTimeInMs);
        this.timeLeft = timer(0, this.timerValue).pipe(
            map(n => {
                console.info("n = " + n);
                return (this.automaticLogoutTimeInMs) - (n * this.timerStep);
            }),
            takeWhile(n => n >= 0) 
        );

        this.timeLeftSubscription = this.timeLeft.subscribe(n => {
            console.info("time left: " + n);
            if (n === this.warningBeforeLogoutInMs) {
                this.logoutComing = true;
            }
            
            if (n === 0) {
                this.dialog.open(InfoDialog, { data: { title: 'Automatic Logout', text: 'You have been logged out due to inactivity.', type: 'information' } });
                this.sharedData.logout();
            }
        });
    }
}