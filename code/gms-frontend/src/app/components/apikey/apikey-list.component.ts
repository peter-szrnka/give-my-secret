import { Component } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { ApiKey, PAGE_CONFIG_API_KEY } from "./model/apikey.model";
import { PageConfig } from "../../common/model/common.model";
import { ApiKeyService } from "./service/apikey-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";
import { ClipboardService } from "../../common/service/clipboard-service";

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
        public override dialog: MatDialog,
        override activatedRoute: ActivatedRoute,
        private readonly clipboardService: ClipboardService) {
        super(router, sharedData, service, dialog, activatedRoute);
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