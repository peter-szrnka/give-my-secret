import { Component } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseSaveableDetailComponent } from "../../common/components/abstractions/base-saveable-detail.component";
import { ButtonConfig } from "../../common/components/nav-back/button-config";
import { ApiKey, PAGE_CONFIG_API_KEY } from "../../common/model/apikey.model";
import { PageConfig } from "../../common/model/common.model";
import { ApiKeyService } from "../../common/service/apikey-service";
import { SharedDataService } from "../../common/service/shared-data-service";

@Component({
    selector: 'api-key-detail',
    templateUrl: './apikey-detail.component.html',
    styleUrls: ['./apikey-detail.component.scss']
})
export class ApiKeyDetailComponent extends BaseSaveableDetailComponent<ApiKey, ApiKeyService> {

    buttonConfig: ButtonConfig[] = [
        { primary: true, url: '/apikey/list', label: 'Back to list' }
    ];

    constructor(
        protected override router: Router,
        protected override sharedData: SharedDataService,
        protected override service: ApiKeyService,
        public override dialog: MatDialog,
        protected override activatedRoute: ActivatedRoute) {
        super(router, sharedData, service, dialog, activatedRoute);
    }

    override getPageConfig(): PageConfig {
        return PAGE_CONFIG_API_KEY;
    }
}