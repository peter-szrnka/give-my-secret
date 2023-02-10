import { Component } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { ApiKey, PAGE_CONFIG_API_KEY } from "./model/apikey.model";
import { PageConfig } from "../../common/model/common.model";
import { ApiKeyService } from "./service/apikey-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'apikey-list',
    templateUrl: './apikey-list.component.html',
    styleUrls : ['./apikey-list.component.scss']
})
export class ApiKeyListComponent extends BaseListComponent<ApiKey, ApiKeyService> {
    apiKeyColumns: string[] = ['id', 'name', 'status', 'creationDate', 'operations'];
    
    constructor(
      override router : Router,
      override sharedData : SharedDataService, 
      override service : ApiKeyService,
      public override dialog: MatDialog,
      override activatedRoute: ActivatedRoute) {
        super(router, sharedData, service, dialog, activatedRoute);
    }

    getPageConfig(): PageConfig {
        return PAGE_CONFIG_API_KEY;
    }
}