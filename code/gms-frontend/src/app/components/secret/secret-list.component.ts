import { Component } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { PageConfig } from "../../common/model/common.model";
import { PAGE_CONFIG_SECRET, Secret } from "./model/secret.model";
import { SecretService } from "./service/secret-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";
import { ClipboardService } from "../../common/service/clipboard-service";

export const COPY_SECRET_ID_MESSAGE = "Secret ID copied to clipboard!";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'secret-list',
    templateUrl: './secret-list.component.html',
    styleUrls : ['./secret-list.component.scss']
})
export class SecretListComponent extends BaseListComponent<Secret, SecretService> {
    secretColumns: string[] = ['id', 'secretId', 'status', 'lastUpdated', 'lastRotated', 'rotationPeriod', 'operations'];

    constructor(
      override router : Router,
      override sharedData : SharedDataService, 
      override service : SecretService,
      public override dialog: MatDialog,
      override activatedRoute: ActivatedRoute,
      private clipboardService: ClipboardService) {
        super(router, sharedData, service, dialog, activatedRoute);
    }

    getPageConfig(): PageConfig {
      return PAGE_CONFIG_SECRET;
    }

    /**
     * Copies a secretId value to the clipboard
     * @param value Input value
     */
    public copySecretIdValue(value: string) {
        this.clipboardService.copyValue(value, COPY_SECRET_ID_MESSAGE);
    }
}