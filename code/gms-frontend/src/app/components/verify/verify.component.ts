import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { catchError, Observable, of } from "rxjs";
import { DialogData } from "../../common/components/info-dialog/dialog-data.model";
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { AuthenticationPhase, Login, LoginResponse, VerifyLogin } from "../../common/model/login.model";
import { AuthService } from "../../common/service/auth-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'verify',
    templateUrl: './verify.component.html',
    styleUrls: ['./verify.component.scss']
})
export class VerifyComponent implements OnInit {

    constructor(
        private router: Router,
        private authService: AuthService,
        private sharedDataService: SharedDataService,
        private splashScreenStateService: SplashScreenStateService,
        private dialog: MatDialog) {

        this.formModel.username = this.router.getCurrentNavigation()?.extras?.state?.['username'];
    }

    formModel: VerifyLogin = {
        username: undefined,
        verificationCode: undefined
    };

    ngOnInit(): void {
        this.splashScreenStateService.stop();
    }

    verifyLogin(): void {
        this.splashScreenStateService.start();

        this.authService.verifyLogin(this.formModel)
            .pipe(catchError((err) => this.handleError(err)))
            .subscribe((response : LoginResponse) => {
                if (response === null) {
                    this.splashScreenStateService.stop();
                    this.showErrorModal();
                    return;
                }

                if (response.phase !== AuthenticationPhase.COMPLETED) {
                    this.splashScreenStateService.stop();
                    this.showErrorModal();
                    this.router.navigateByUrl('/login');
                    return;
                }

                this.splashScreenStateService.stop();
                this.sharedDataService.setCurrentUser(response.currentUser);
                void this.router.navigate(['']);
            });
    }

    private showErrorModal() {
        this.dialog.open(InfoDialog, {
            width: '250px',
            data: { text : "Login verification failed!", type : 'warning' } as DialogData
        });
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    private handleError(err : any): Observable<any> {  
        console.error("Unexpected error occurred during login", err);
        return of(null);
    }
}