import { Component } from "@angular/core";
import { Router } from "@angular/router";
import { DialogService } from "../../common/service/dialog-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { ResetPasswordRequestService } from "./service/request-password-reset.service";

@Component({
    selector: 'request-password-reset',
    templateUrl: './request-password-reset.component.html',
    standalone: false
})
export class RequestPasswordResetComponent {

    username: string;

    constructor(
        private readonly router: Router,
        private readonly service : ResetPasswordRequestService, 
        private readonly splashScreenStateService: SplashScreenStateService, 
        private readonly dialogService: DialogService) {}

    requestReset() : void {
        this.splashScreenStateService.start();
        this.service.requestPasswordReset(this.username).subscribe({
            next: () => {
                this.dialogService.openNewDialog({ text: "dialog.passwordRequestSent", type: "information" });
                this.splashScreenStateService.stop();
                this.router.navigate(['/login']);
            },
            error: () => {
                this.splashScreenStateService.stop();
                this.dialogService.openNewDialog({ text: "dialog.failedToSendPasswordReset", type: "warning" });
            },
        });
    }
}