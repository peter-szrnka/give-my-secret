import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { ActivatedRoute, NavigationExtras, Router, RouterLink } from "@angular/router";
import { catchError, takeUntil } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { BaseLoginComponent } from "../../common/components/abstractions/component/base-login.component";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { AuthenticationPhase, Login, LoginResponse } from "../../common/model/login.model";
import { AuthService } from "../../common/service/auth-service";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'login',
    templateUrl: './login.component.html',
    standalone: true,
    imports: [TranslatorModule, RouterLink, AngularMaterialModule, FormsModule]
})
export class LoginComponent extends BaseLoginComponent {

    formModel: Login = {
        username: undefined,
        credential: undefined
    };
    loginAttempt: boolean = false;
    showPassword: boolean = false;

    constructor(
        protected override route: ActivatedRoute,
        protected router: Router,
        private readonly authService: AuthService,
        protected override sharedDataService: SharedDataService,
        protected override splashScreenStateService: SplashScreenStateService,
        protected override dialogService: DialogService) {
        super(route, sharedDataService, dialogService, splashScreenStateService)
    }

    login(): void {
        this.splashScreenStateService.start();
        this.loginAttempt = true;

        this.authService.login(this.formModel)
            .pipe(catchError((err) => this.handleError(err)), takeUntil(this.destroy$))
            .subscribe((response: LoginResponse) => {
                this.loginAttempt = false;
                this.splashScreenStateService.stop();
                if (response === null) {
                    this.displayErrorModal();
                    return;
                }

                if (response.phase === AuthenticationPhase.MFA_REQUIRED) {
                    const navigationExtras: NavigationExtras = {
                        state: {
                            username: response.currentUser.username
                        },
                        queryParams: {
                            previousUrl: this.route.snapshot.queryParams['previousUrl'] ?? ''
                        }
                    };

                    this.router.navigate(['/verify'], navigationExtras);
                    return;
                } else if (response.phase === AuthenticationPhase.BLOCKED) {
                    this.displayErrorModal();
                    return;
                }

                this.finalizeSuccessfulLogin(response.currentUser);
            });
    }

    togglePasswordDisplay(): void {
        this.showPassword = !this.showPassword;
    }
}