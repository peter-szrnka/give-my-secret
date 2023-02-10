import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { ApiTestingService } from "./service/api-testing-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { SecureStorageService } from "../../common/service/secure-storage.service";
import { CredentialPairApiResponse } from "../secret/model/credential-pair-api-response.model";
import { CredentialApiResponse } from "../secret/model/credential-api-response.model";
/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'api-testing-component',
    templateUrl: './api-testing.component.html',
    styleUrls: ['./api-testing.component.scss']
})
export class ApiTestingComponent implements OnInit {

    apiKey : string;
    secretId : string;
    apiResponse: string | undefined;

    constructor(
        private service: ApiTestingService,
        private dialog: MatDialog,
        private splashScreenService: SplashScreenStateService,
        private secureStorageService : SecureStorageService
    ) { }

    ngOnInit(): void {
        this.apiKey = this.secureStorageService.getItem('apiKey');
        this.secretId = this.secureStorageService.getItem('secretId');
    }

    callApi() {
        this.splashScreenService.start();
        this.apiResponse = undefined;
        this.service.getSecretValue(this.secretId, this.apiKey)
            .subscribe({
                next: (response : CredentialApiResponse | CredentialPairApiResponse) => {
                    this.apiResponse = JSON.stringify(response);

                    this.secureStorageService.setItem('apiKey', this.apiKey);
                    this.secureStorageService.setItem('secretId', this.secretId);

                    this.splashScreenService.stop();
                },
                error: (err: any) => {
                    this.dialog.open(InfoDialog, { data: { text: "Unexpected error occurred: " + getErrorMessage(err), type : "warning" } });
                    this.splashScreenService.stop();
                }
            });
    }
}