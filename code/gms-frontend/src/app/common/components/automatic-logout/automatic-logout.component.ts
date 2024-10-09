import { DatePipe, NgClass } from "@angular/common";
import { Component, Input, OnDestroy, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { map, Observable, Subscription, takeWhile, timer } from "rxjs";
import { AngularMaterialModule } from "../../../angular-material-module";
import { SharedDataService } from "../../service/shared-data-service";
import { InfoDialog } from "../info-dialog/info-dialog.component";

export const WARNING_THRESHOLD = 60000;

/**
 * @author Peter Szrnka
 */
@Component({
    standalone: true,
    imports: [
        AngularMaterialModule,
        DatePipe,
        NgClass
    ],
    selector: 'automatic-logout',
    templateUrl: './automatic-logout.component.html',
    styleUrls: ['./automatic-logout.component.scss']
})
export class AutomaticLogoutComponent implements OnInit, OnDestroy {

    @Input() automaticLogoutTimeInMinutes: number;
    timeLeftValue: number;
    resetTimerSubscription: Subscription;
    timeLeftSubscription: Subscription;
    logoutComing: boolean = false;

    constructor(private sharedData: SharedDataService, private dialog: MatDialog) { }

    ngOnInit(): void {
        this.resetTimerSubscription = this.sharedData.resetTimerSubject$.subscribe((oldStartTime) => {
            this.timeLeftSubscription?.unsubscribe();
            this.timeLeftValue = (this.automaticLogoutTimeInMinutes*1000*60) - ((Date.now() - (oldStartTime ?? (Date.now()))));
            this.initiateTimer();
        });
    }

    ngOnDestroy(): void {
        this.timeLeftSubscription?.unsubscribe();
        this.resetTimerSubscription?.unsubscribe();
    }

    initiateTimer(): void {
        if (this.timeLeftValue <= 0) {
            return;
        }

        this.sharedData.setStartTime(Date.now());

        const timeLeftObservable: Observable<number> = timer(0, 1000).pipe(
            map(n => this.timeLeftValue - 1000),
            takeWhile(n => n >= 0) 
        );

        this.timeLeftSubscription = timeLeftObservable.subscribe(n => {
            this.logoutComing = (n <= WARNING_THRESHOLD);
            this.timeLeftValue = n;
            
            if (n === 0) {
                this.dialog.open(InfoDialog, { data: { title: 'Automatic Logout', text: 'You have been logged out due to inactivity.', type: 'information' } });
                this.sharedData.logout();
            }
        });
    }
}