import { EventEmitter, Injectable, Output } from "@angular/core";
import { Router } from "@angular/router";
import { catchError, Observable, of, ReplaySubject } from "rxjs";
import { SystemReadyData } from "../model/system-ready.model";
import { SystemStatusDto } from "../model/system-status.model";
import { User } from "../../components/user/model/user.model";
import { AuthService } from "./auth-service";
import { SetupService } from "../../components/setup/service/setup-service";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'any' })
export class SharedDataService {

    userSubject$: ReplaySubject<User | undefined> = new ReplaySubject<User | undefined>();
    systemReadySubject$: ReplaySubject<SystemReadyData> = new ReplaySubject<SystemReadyData>();
    systemReady?: boolean = undefined;
    authMode: string;

    @Output() messageCountUpdateEvent = new EventEmitter<number>();

    constructor(private router: Router, private setupService: SetupService, private authService : AuthService) {
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    public setCurrentUser(currentUser: User) {
        localStorage.setItem('currentUser', JSON.stringify(currentUser));
        this.userSubject$.next(currentUser);
    }

    public clearData() {
        this.userSubject$.next(undefined);
        localStorage.removeItem('currentUser');
    }

    public logout() {
        if (this.router.url === '/login') {
            return;
        }

        // eslint-disable-next-line @typescript-eslint/no-empty-function
        this.authService.logout().subscribe(() => {
            this.clearData();
            this.router.navigate(['/login']);
        });
    }

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
                    this.systemReadySubject$.next({ ready : false, status: 0 });
                    return;
                }

                this.systemReady = "OK" === response.status;
                this.authMode = response.authMode;
                this.systemReadySubject$.next({ ready : this.systemReady, status: 200 });
            });
        }

        this.userSubject$.next(this.getUserInfo());
    }

    getUserInfo(): User | undefined {
        const currentUserData : string = localStorage.getItem('currentUser') || '';

        if (currentUserData === '') {
            return undefined;
        }

        return JSON.parse(currentUserData);
    }
}