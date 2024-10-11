import { Component, Inject } from "@angular/core";
import { Router } from "@angular/router";
import { SetupService } from "./service/setup-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { WINDOW_TOKEN } from "../../window.provider";
import { UserData, EMPTY_USER_DATA } from "../user/model/user-data.model";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'setup-component',
    templateUrl: './setup.component.html'
})
export class SetupComponent {

    userData : UserData = EMPTY_USER_DATA;
    public errorMessage : string | undefined = undefined;

    constructor(
        @Inject(WINDOW_TOKEN) private readonly window: Window,
        private readonly router : Router, 
        private readonly splashScreenService : SplashScreenStateService,
        private readonly setupService : SetupService) {}

    saveAdminUser() {
        this.splashScreenService.start();
        this.userData.role = 'ROLE_ADMIN';
        this.setupService.saveAdminUser(this.userData)
        .subscribe({
            next: () => {
                this.splashScreenService.stop();
                this.errorMessage = '';
            },
            error: (err) => {
                this.splashScreenService.stop();
                if (err.status === 404) {
                    void this.router.navigate(['']);
                } else {
                    this.errorMessage = getErrorMessage(err);
                }
            }
        });
    }

    retrySetup() : void {
        this.errorMessage = undefined;
    }

    navigateToHome() : void {
        this.window.location.reload();
    }
}