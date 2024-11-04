import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import randomstring from "randomstring";
import { BaseSaveableDetailComponent } from "../../common/components/abstractions/component/base-saveable-detail.component";
import { ButtonConfig } from "../../common/components/nav-back/button-config";
import { PageConfig } from "../../common/model/common.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { ApiKey, PAGE_CONFIG_API_KEY } from "./model/apikey.model";
import { ApiKeyService } from "./service/apikey-service";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'api-key-detail',
    templateUrl: './apikey-detail.component.html'
})
export class ApiKeyDetailComponent extends BaseSaveableDetailComponent<ApiKey, ApiKeyService> {

    buttonConfig: ButtonConfig[] = [
        { primary: true, url: '/apikey/list', label: 'navback.back2List' }
    ];

    constructor(
        protected override router: Router,
        protected override sharedData: SharedDataService,
        protected override service: ApiKeyService,
        public override dialogService: DialogService,
        protected override activatedRoute: ActivatedRoute,
        protected override splashScreenStateService: SplashScreenStateService) {
        super(router, sharedData, service, dialogService, activatedRoute, splashScreenStateService);
    }

    override getPageConfig(): PageConfig {
        return PAGE_CONFIG_API_KEY;
    }

    generateRandomValue() : void {
        this.data.value = randomstring.generate(32);
    }
}