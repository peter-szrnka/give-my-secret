import { Directive, OnInit } from "@angular/core";
import { Observable, of } from "rxjs";
import { DialogData } from "../../info-dialog/dialog-data.model";
import { InfoDialog } from "../../info-dialog/info-dialog.component";
import { MatDialog } from "@angular/material/dialog";
import { SplashScreenStateService } from "../../../service/splash-screen-service";
import { Router } from "@angular/router";
import { LoginResponse } from "../../../model/login.model";
import { SharedDataService } from "../../../service/shared-data-service";

/**
 * @author Peter Szrnka
 */
@Directive()
export abstract class BaseLoginComponent implements OnInit {

    constructor(
        protected router: Router,
        protected sharedDataService: SharedDataService,
        protected dialog: MatDialog,
        protected splashScreenStateService: SplashScreenStateService
    ) {}

    ngOnInit(): void {
        this.splashScreenStateService.stop();
    }
    
    protected showErrorModal() {
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

    protected finalizeSuccessfulLogin(response : LoginResponse) {
        this.splashScreenStateService.stop();
        this.sharedDataService.setCurrentUser(response.currentUser);
        void this.router.navigate(['']);
    }
}