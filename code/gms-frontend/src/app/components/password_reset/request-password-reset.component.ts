import { Component } from "@angular/core";
import { ResetPasswordRequestService } from "./service/request-password-reset.service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { MatDialog } from "@angular/material/dialog";
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { DialogData } from "../../common/components/info-dialog/dialog-data.model";
import { Router } from "@angular/router";

@Component({
    selector: 'request-password-reset',
    templateUrl: './request-password-reset.component.html',
    styleUrls: ['./request-password-reset.component.scss']
})
export class RequestPasswordResetComponent {

    username: string;

    constructor(
        private readonly router: Router,
        private readonly service : ResetPasswordRequestService, 
        private readonly splashScreenStateService: SplashScreenStateService, 
        private readonly dialog: MatDialog) {}

    requestReset() : void {
        this.splashScreenStateService.start();
        this.service.requestPasswordReset(this.username).subscribe({
            next: () => {
                this.showSuccessModal();
                this.splashScreenStateService.stop();
                this.router.navigate(['/login']);
            },
            error: () => {
                this.splashScreenStateService.stop();
                this.showErrorModal();
            },
        });
    }

    private showSuccessModal() {
        this.dialog.open(InfoDialog, {
            width: '250px',
            data: { text : "Password request sent to admins!", type : 'info' } as DialogData
        });
    }

    private showErrorModal() {
        this.dialog.open(InfoDialog, {
            width: '250px',
            data: { text : "Failed to request password reset!", type : 'warning' } as DialogData
        });
    }
}