import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { Router, RouterModule } from "@angular/router";
import { takeUntil } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { BaseComponent } from "../../common/components/abstractions/component/base.component";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { DialogService } from "../../common/service/dialog-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { ResetPasswordRequestService } from "./service/request-password-reset.service";

@Component({
    selector: 'request-password-reset',
    templateUrl: './request-password-reset.component.html',
    imports: [AngularMaterialModule, RouterModule, FormsModule, TranslatorModule]
})
export class RequestPasswordResetComponent extends BaseComponent {

    username: string;

    constructor(
        private readonly router: Router,
        private readonly service : ResetPasswordRequestService, 
        private readonly splashScreenStateService: SplashScreenStateService, 
        private readonly dialogService: DialogService) {
            super();
        }

    requestReset() : void {
        this.splashScreenStateService.start();
        this.service.requestPasswordReset(this.username).pipe(takeUntil(this.destroy$)).subscribe({
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