import { DatePipe, NgClass } from "@angular/common";
import { Component, CUSTOM_ELEMENTS_SCHEMA, Input, NO_ERRORS_SCHEMA, OnDestroy, OnInit } from "@angular/core";
import { MatTooltipModule } from "@angular/material/tooltip";
import { map, Observable, Subscription, takeWhile, timer } from "rxjs";
import { DialogService } from "../../service/dialog-service";
import { SharedDataService } from "../../service/shared-data-service";

export const WARNING_THRESHOLD = 60000;

/**
 * @author Peter Szrnka
 */
@Component({
    standalone: true,
    imports: [
        MatTooltipModule,
        DatePipe,
        NgClass
    ],
    schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
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

    constructor(private readonly sharedData: SharedDataService, private readonly dialogService: DialogService) {}

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
            map(() => this.timeLeftValue - 1000),
            takeWhile(n => n >= 0) 
        );

        this.timeLeftSubscription = timeLeftObservable.subscribe(n => {
            this.logoutComing = (n <= WARNING_THRESHOLD);
            this.timeLeftValue = n;
            
            if (n === 0) {
                this.dialogService.openNewDialog({ title: "automaticLogout.title", text: "automaticLogout.logout", type: "information" });
                this.sharedData.logout();
            }
        });
    }
}