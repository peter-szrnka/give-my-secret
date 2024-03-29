import { Component, Inject } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
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
        protected override route: ActivatedRoute,
        protected override router: Router,
        private authService: AuthService,
        protected override sharedDataService: SharedDataService,
        protected override splashScreenStateService: SplashScreenStateService,
        protected override dialog: MatDialog) {
            super(route, router, sharedDataService, dialog, splashScreenStateService);      
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

    async verifyLogin(): Promise<void> {
        this.splashScreenStateService.start();

        try {
            const response: LoginResponse = await firstValueFrom(this.authService.verifyLogin(this.formModel));
            this.handleResponse(response);
        } catch (err) {
            this.handleFailure();
        }
    }

    private handleResponse(response: LoginResponse): void {
        if (response.phase !== AuthenticationPhase.COMPLETED) {
            this.handleFailure();
            return;
        }

        this.finalizeSuccessfulLogin(response.currentUser);
    }

    private handleFailure(): void {
        this.displayErrorModal();
        this.router.navigateByUrl('/login');
    }
}
