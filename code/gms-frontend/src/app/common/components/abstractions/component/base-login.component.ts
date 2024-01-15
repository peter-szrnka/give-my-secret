import { Directive, OnInit } from "@angular/core";
import { Observable, of } from "rxjs";
import { DialogData } from "../../info-dialog/dialog-data.model";
import { InfoDialog } from "../../info-dialog/info-dialog.component";
import { MatDialog } from "@angular/material/dialog";
import { SplashScreenStateService } from "../../../service/splash-screen-service";
import { ActivatedRoute, Router } from "@angular/router";
import { SharedDataService } from "../../../service/shared-data-service";

/**
 * @author Peter Szrnka
 */
@Directive()
export abstract class BaseLoginComponent implements OnInit {

    constructor(
        protected route: ActivatedRoute,
        protected router: Router,
        protected sharedDataService: SharedDataService,
        protected dialog: MatDialog,
        protected splashScreenStateService: SplashScreenStateService
    ) {}

    ngOnInit(): void {
        this.splashScreenStateService.stop();
    }
    
    private showErrorModal() {
        this.dialog.open(InfoDialog, {
            width: '250px',
            data: { text : "Login failed!", type : 'warning' } as DialogData
        });
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    protected handleError(err : any): Observable<any> {  
        console.error("Unexpected error occurred during login", err);
        return of(null);
    }

    protected displayErrorModal() {
        this.splashScreenStateService.stop();
        this.showErrorModal();
    }

    protected finalizeSuccessfulLogin() {
        this.splashScreenStateService.stop();
        this.sharedDataService.refreshCurrentUserInfo();
        const nextUrl = this.route.snapshot.queryParams['previousUrl'] ?? '';
        void this.router.navigate([nextUrl]);
    }
}