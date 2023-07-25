import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { NavigationExtras, Router } from "@angular/router";
import { catchError, Observable, of } from "rxjs";
import { DialogData } from "../../common/components/info-dialog/dialog-data.model";
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { AuthenticationPhase, Login, LoginResponse } from "../../common/model/login.model";
import { User } from "../user/model/user.model";
import { AuthService } from "../../common/service/auth-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

    constructor(
        private router: Router,
        private authService: AuthService,
        private sharedDataService: SharedDataService,
        private splashScreenStateService: SplashScreenStateService,
        private dialog: MatDialog) {
    }

    formModel: Login = {
        username: undefined,
        credential: undefined
    };

    ngOnInit(): void {
        this.splashScreenStateService.stop();
    }

    login(): void {
        this.splashScreenStateService.start();

        this.authService.login(this.formModel)
            .pipe(catchError((err) => this.handleError(err)))
            .subscribe((response : LoginResponse) => {
                if (response === null) {
                    this.splashScreenStateService.stop();
                    this.showErrorModal();
                    return;
                }

                if (response.phase === AuthenticationPhase.MFA_REQUIRED) {
                    this.splashScreenStateService.stop();

                    const navigationExtras: NavigationExtras = {
                        state: {
                          username: response.currentUser.username
                        }
                      };

                    this.router.navigate(['/verify'], navigationExtras);
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
            data: { text : "Login failed!", type : 'warning' } as DialogData
        });
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    private handleError(err : any): Observable<any> {  
        console.error("Unexpected error occurred during login", err);
        return of(null);
    }
}