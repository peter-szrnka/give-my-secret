import { EventEmitter, Injectable, Output } from "@angular/core";
import { Router } from "@angular/router";
import { catchError, Observable, of, ReplaySubject } from "rxjs";
import { SystemReadyData } from "../model/system-ready.model";
import { SystemStatusDto } from "../model/system-status.model";
import { User } from "../../components/user/model/user.model";
import { AuthService } from "./auth-service";
import { SetupService } from "../../components/setup/service/setup-service";
import { InformationService } from "./info-service";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'any' })
export class SharedDataService {

    currentUser: User | undefined;
    userSubject$: ReplaySubject<User | undefined> = new ReplaySubject<User | undefined>();
    systemReadySubject$: ReplaySubject<SystemReadyData> = new ReplaySubject<SystemReadyData>();
    authModeSubject$ : ReplaySubject<string> = new ReplaySubject<string>();
    systemReady?: boolean = undefined;
    authMode: string;

    @Output() messageCountUpdateEvent = new EventEmitter<number>();
    @Output() showLargeMenuEvent = new EventEmitter<boolean>();

    constructor(
        private router: Router, 
        private setupService: SetupService, 
        private authService: AuthService,
        private infoService: InformationService
    ) {}

    public refreshCurrentUserInfo(): void {
        this.infoService.getUserInfo().then((user: User | null) => {
            this.currentUser = user ?? undefined;
            this.userSubject$.next(this.currentUser);
        });
    }

    public clearData() {
        this.userSubject$.next(undefined);
    }

    public logout() {
        if (this.router.url === '/login') {
            return;
        }

        void this.router.navigate(['/login']);
        this.authService.logout().subscribe(() => this.clearData());
    }

    /**
     * @deprecated
     */
    public clearDataAndReturn(data : any) : Observable<any> {
        this.clearData();
        return of(data);
    }

    public check() {
        if (this.systemReady === undefined) {
            this.setupService.checkReady().pipe(catchError(() => {
                return of({ status: 'FAIL' } as SystemStatusDto);
              })).subscribe(response => {
                if (response.status === "FAIL") {
                    this.systemReadySubject$.next({ ready : false, status: 0, authMode : response.authMode });
                    return;
                }

                this.systemReady = "OK" === response.status;
                this.authModeSubject$.next(response.authMode);
                this.authMode = response.authMode;
                this.systemReadySubject$.next({ ready : this.systemReady, status: 200, authMode : response.authMode });
            });
        }

        this.refreshCurrentUserInfo();
    }

    async getUserInfo(): Promise<User | undefined> {
        if (!this.currentUser) {
            this.currentUser = await this.infoService.getUserInfo() ?? undefined;
            this.userSubject$.next(this.currentUser);
        }

        return this.currentUser;
    }
}