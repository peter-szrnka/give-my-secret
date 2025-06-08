import { Component, Inject } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { firstValueFrom } from "rxjs";
import { BaseLoginComponent } from "../../common/components/abstractions/component/base-login.component";
import { AuthenticationPhase, LoginResponse, VerifyLogin } from "../../common/model/login.model";
import { AuthService } from "../../common/service/auth-service";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { WINDOW_TOKEN } from "../../window.provider";
import { LoggerService } from "../../common/service/logger-service";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { FormsModule } from "@angular/forms";
import { AngularMaterialModule } from "../../angular-material-module";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'verify',
    templateUrl: './verify.component.html',
    styleUrls: ['./verify.component.scss'],
    imports: [AngularMaterialModule, FormsModule, InformationMessageComponent, TranslatorModule]
})
export class VerifyComponent extends BaseLoginComponent {

    constructor(
        @Inject(WINDOW_TOKEN) private readonly window: Window,
        protected override route: ActivatedRoute,
        protected router: Router,
        private readonly authService: AuthService,
        protected override sharedDataService: SharedDataService,
        protected override splashScreenStateService: SplashScreenStateService,
        protected override dialogService: DialogService,
        protected loggerService: LoggerService) {
            super(route, sharedDataService, dialogService, splashScreenStateService);      
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
            this.loggerService.error('Error during login verification', err);
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
