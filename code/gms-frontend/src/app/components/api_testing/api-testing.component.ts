import { Component, OnInit } from "@angular/core";
import { DialogService } from "../../common/service/dialog-service";
import { SecureStorageService } from "../../common/service/secure-storage.service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { CredentialApiResponse } from "../secret/model/credential-api-response.model";
import { ApiTestingService } from "./service/api-testing-service";
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
        private dialogService: DialogService,
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
                next: (response : CredentialApiResponse | { [key:string] : string }) => {
                    this.apiResponse = JSON.stringify(response);

                    this.secureStorageService.setItem('apiKey', this.apiKey);
                    this.secureStorageService.setItem('secretId', this.secretId);

                    this.splashScreenService.stop();
                },
                error: (err: any) => {
                    this.dialogService.openWarningDialog("Unexpected error occurred: " + getErrorMessage(err));
                    this.splashScreenService.stop();
                }
            });
    }
}