import { Component } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { ApiTestingService } from "../../common/service/api-testing-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { getErrorMessage } from "../../common/utils/error-utils";

@Component({
    selector: 'api-testing-component',
    templateUrl: './api-testing.component.html',
    styleUrls: ['./api-testing.component.scss']
})
export class ApiTestingComponent {

    apiKey = localStorage.getItem('apiKey') || '';
    secretId = localStorage.getItem('secretId') || '';
    apiResponse?: string;

    constructor(
        private service: ApiTestingService,
        private dialog: MatDialog,
        private splashScreenService: SplashScreenStateService
    ) { }

    callApi() {
        this.splashScreenService.start();
        this.apiResponse = undefined;
        this.service.getSecretValue(this.secretId, this.apiKey)
            .subscribe({
                next: (response) => {
                    this.apiResponse = response.value;
                },
                error: (err: any) => {
                    this.dialog.open(InfoDialog, { data: { text: "Unexpected error occurred: " + getErrorMessage(err), type : "warning" } });
                    this.splashScreenService.stop();
                },
                complete: () => {
                    localStorage.setItem('apiKey', this.apiKey);
                    localStorage.setItem('secretId', this.secretId);

                    this.splashScreenService.stop();
                }
            });
    }
}