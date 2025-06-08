import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { takeUntil } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { BaseComponent } from "../../common/components/abstractions/component/base.component";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { DialogService } from "../../common/service/dialog-service";
import { SecureStorageService } from "../../common/service/secure-storage.service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { getErrorCode } from "../../common/utils/error-utils";
import { CredentialApiResponse } from "../secret/model/credential-api-response.model";
import { User } from "../user/model/user.model";
import { ApiTestingService } from "./service/api-testing-service";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'api-testing-component',
    templateUrl: './api-testing.component.html',
    imports: [
        AngularMaterialModule,
        FormsModule,
        TranslatorModule,
        InformationMessageComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ApiTestingComponent extends BaseComponent implements OnInit {

    apiKey : string;
    secretId : string;
    apiResponse: string | undefined;
    userName: string;

    constructor(
        private readonly sharedData: SharedDataService,
        private readonly service: ApiTestingService,
        private readonly dialogService: DialogService,
        private readonly splashScreenService: SplashScreenStateService,
        private readonly secureStorageService : SecureStorageService
    ) {
        super();
    }

    ngOnInit(): void {
        this.sharedData.getUserInfo().then((user: User | undefined) => {
            this.userName = user?.username ?? '';
            this.apiKey = this.secureStorageService.getItem(this.userName, 'apiKey');
            this.secretId = this.secureStorageService.getItem(this.userName, 'secretId');
        });
    }

    callApi() {
        this.splashScreenService.start();
        this.apiResponse = undefined;
        this.service.getSecretValue(this.secretId, this.apiKey)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response : CredentialApiResponse | { [key:string] : string }) => {
                    this.apiResponse = JSON.stringify(response);

                    this.secureStorageService.setItem(this.userName, 'apiKey', this.apiKey);
                    this.secureStorageService.setItem(this.userName, 'secretId', this.secretId);

                    this.splashScreenService.stop();
                },
                error: (err: any) => {
                    this.dialogService.openNewDialog({ text: "dialog.save.error", type: "warning", errorCode: getErrorCode(err) });
                    this.splashScreenService.stop();
                }
            });
    }
}