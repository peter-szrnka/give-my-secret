import { EventEmitter, Injectable, Output } from "@angular/core";
import { Router } from "@angular/router";
import { firstValueFrom, ReplaySubject } from "rxjs";
import { SetupService } from "../../components/setup/service/setup-service";
import { User } from "../../components/user/model/user.model";
import { SystemReadyData } from "../model/system-ready.model";
import { SystemStatus } from "../model/system-status.model";
import { AuthService } from "./auth-service";
import { InformationService } from "./info-service";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class SharedDataService {

    currentUser: User | undefined;
    userSubject$: ReplaySubject<User | undefined> = new ReplaySubject<User | undefined>();
    systemReadySubject$: ReplaySubject<SystemReadyData> = new ReplaySubject<SystemReadyData>();
    authModeSubject$: ReplaySubject<string> = new ReplaySubject<string>();
    resetTimerSubject$: ReplaySubject<number | undefined> = new ReplaySubject<number | undefined>();
    systemReady?: boolean = undefined;
    startTime?: number;

    @Output() messageCountUpdateEvent = new EventEmitter<number>();
    @Output() showLargeMenuEvent = new EventEmitter<boolean>();
    @Output() navigationChangeEvent = new EventEmitter<string>();

    constructor(
        private readonly router: Router,
        private readonly setupService: SetupService,
        private readonly authService: AuthService,
        private readonly infoService: InformationService
    ) { }

    public refreshCurrentUserInfo(): void {
        this.infoService.getUserInfo().then((user: User | null) => {
            this.currentUser = user ?? undefined;
            this.userSubject$.next(this.currentUser);
        });
    }

    public clearData() {
        this.startTime = undefined;
        this.userSubject$.next(undefined);
    }

    public logout() {
        if (this.router.url === '/login') {
            return;
        }

        this.authService.logout().subscribe(() => this.clearData());
    }

    public check() {
        if (this.systemReady === undefined) {
            this.checkSystemReady();
        }
    }

    public checkSystemReady() {
        firstValueFrom(this.setupService.checkReady())
            .then((response: SystemStatus) => {
                this.systemReady = "OK" === response.status;
                this.authModeSubject$.next(response.authMode);
                this.systemReadySubject$.next({ 
                    ready: this.systemReady, 
                    systemStatus: response.status,
                    status: 200, 
                    authMode: response.authMode, 
                    automaticLogoutTimeInMinutes: response.automaticLogoutTimeInMinutes 
                });
                this.refreshCurrentUserInfo();
            })
            .catch(() => {
                const authMode = 'N/A';
                this.systemReadySubject$.next({ ready: false, systemStatus: 'FAILURE', status: 0, authMode: authMode });
                this.systemReady = false;
            });
    }

    setStartTime(currentTime?: number) {
        this.startTime = this.startTime ?? currentTime;
    }

    resetAutomaticLogoutTimer(clearStartTime: boolean = true) {
        this.resetTimerSubject$.next(this.startTime);

        if (clearStartTime) {
            this.startTime = undefined;
        }
    }

    async getUserInfo(): Promise<User | undefined> {
        if (!this.currentUser) {
            this.currentUser = await this.infoService.getUserInfo();
            this.userSubject$.next(this.currentUser);
        }

        return this.currentUser;
    }
}