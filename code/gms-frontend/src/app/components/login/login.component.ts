import { Component } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { NavigationExtras, Router } from "@angular/router";
import { catchError } from "rxjs";
import { BaseLoginComponent } from "../../common/components/abstractions/component/base-login.component";
import { AuthenticationPhase, Login, LoginResponse } from "../../common/model/login.model";
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
export class LoginComponent extends BaseLoginComponent {

    constructor(
        protected override router: Router,
        private authService: AuthService,
        protected override sharedDataService: SharedDataService,
        protected override splashScreenStateService: SplashScreenStateService,
        protected override dialog: MatDialog) {
            super(router, sharedDataService, dialog, splashScreenStateService)
    }

    formModel: Login = {
        username: undefined,
        credential: undefined
    };
    showPassword: boolean = false;

    login(): void {
        this.splashScreenStateService.start();

        this.authService.login(this.formModel)
            .pipe(catchError((err) => this.handleError(err)))
            .subscribe((response : LoginResponse) => {
                this.splashScreenStateService.stop();
                if (response === null) {
                    this.displayErrorModal();
                    return;
                }

                if (response.phase === AuthenticationPhase.MFA_REQUIRED) {
                    const navigationExtras: NavigationExtras = {
                        state: {
                          username: response.currentUser.username
                        }
                      };

                    this.router.navigate(['/verify'], navigationExtras);
                    return;
                }

                this.finalizeSuccessfulLogin();
            });
    }

    togglePasswordDisplay(): void {
        this.showPassword = !this.showPassword;
    }
}