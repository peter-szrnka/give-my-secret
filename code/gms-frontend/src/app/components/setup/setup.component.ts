import { Component, Inject } from "@angular/core";
import { FormControl, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { UserData, EMPTY_USER_DATA } from "../../common/model/user-data.model";
import { SetupService } from "../../common/service/setup-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { WINDOW_TOKEN } from "../../app.module";

@Component({
    selector: 'setup-component',
    templateUrl: './setup.component.html',
    styleUrls: ['./setup.component.scss']
})
export class SetupComponent {

    emailFormControl = new FormControl('', [Validators.required, Validators.email]);
    userData : UserData = EMPTY_USER_DATA;
    errorMessage : string | undefined = undefined;

    constructor(
        @Inject(WINDOW_TOKEN) private window: Window,
        private router : Router, 
        private splashScreenService : SplashScreenStateService,
        private setupService : SetupService) {}

    saveAdminUser() {
        this.splashScreenService.start();
        this.setupService.saveAdminUser(this.userData)
        .subscribe({
            next: () => {
                this.splashScreenService.stop();
                this.errorMessage = '';
            },
            error: (err) => {
                this.splashScreenService.stop();
                if (err.status === 404) {
                     this.router.navigate(['']);
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