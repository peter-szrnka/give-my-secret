import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";
import { PageConfig } from "../../common/model/common.model";
import { ClipboardService } from "../../common/service/clipboard-service";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { ApiKey, PAGE_CONFIG_API_KEY } from "./model/apikey.model";
import { ApiKeyService } from "./service/apikey-service";

export const COPY_MESSAGE = "Api key value copied to clipboard!";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'apikey-list',
    templateUrl: './apikey-list.component.html',
    styleUrls: ['./apikey-list.component.scss']
})
export class ApiKeyListComponent extends BaseListComponent<ApiKey, ApiKeyService> {
    apiKeyColumns: string[] = ['id', 'name', 'status', 'creationDate', 'operations'];

    constructor(
        override router: Router,
        override sharedData: SharedDataService,
        public override service: ApiKeyService,
        public override dialogService: DialogService,
        override activatedRoute: ActivatedRoute,
        private readonly clipboardService: ClipboardService) {
        super(router, sharedData, service, dialogService, activatedRoute);
    }

    getPageConfig(): PageConfig {
        return PAGE_CONFIG_API_KEY;
    }

    /**
     * Copies a value to the clipboard
     * @param value Input value
     */
    public copyApiKeyValue(value: string) {
        this.clipboardService.copyValue(value, COPY_MESSAGE);
    }
}