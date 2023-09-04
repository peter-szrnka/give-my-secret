import { Component, Inject } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { BaseLoginComponent } from "../../common/components/abstractions/component/base-login.component";
import { AuthenticationPhase, LoginResponse, VerifyLogin } from "../../common/model/login.model";
import { AuthService } from "../../common/service/auth-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { WINDOW_TOKEN } from "../../window.provider";
import { firstValueFrom } from "rxjs";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'verify',
    templateUrl: './verify.component.html',
    styleUrls: ['./verify.component.scss']
})
export class VerifyComponent extends BaseLoginComponent {

    constructor(
        @Inject(WINDOW_TOKEN) private window: Window,
        protected override router: Router,
        private authService: AuthService,
        protected override sharedDataService: SharedDataService,
        public override splashScreenStateService: SplashScreenStateService,
        protected override dialog: MatDialog) {
            super(router, sharedDataService, dialog, splashScreenStateService);      
    }

    formModel: VerifyLogin = {
        username: undefined,
        verificationCode: undefined
    };

    override ngOnInit(): void {
        super.ngOnInit();
        this.formModel.username = this.window.history.state.username;

        if (!this.formModel.username) {
            this.router.navigateByUrl('/');
        }
    }

    handleResponse(response : LoginResponse): void {
        this.splashScreenStateService.stop();
                if (response === null) {
                    this.displayErrorModal();
                    return;
                }

                if (response.phase !== AuthenticationPhase.COMPLETED) {
                    this.showErrorModal();
                    this.router.navigateByUrl('/login');
                    return;
                }

                this.finalizeSuccessfulLogin(response);
    }

    verifyLogin(): void {
        this.splashScreenStateService.start();

        firstValueFrom(this.authService.verifyLogin(this.formModel))
            .then((response : LoginResponse) => {
                this.splashScreenStateService.stop();

                if (response.phase !== AuthenticationPhase.COMPLETED) {
                    this.showErrorModal();
                    this.router.navigateByUrl('/login');
                    return;
                }

                this.finalizeSuccessfulLogin(response);
            })
            .catch(() => {
                this.displayErrorModal();
            });
    }
}
