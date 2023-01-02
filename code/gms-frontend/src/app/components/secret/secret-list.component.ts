import { Component } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseListComponent } from "../../common/components/abstractions/base-list.component";
import { PageConfig } from "../../common/model/common.model";
import { PAGE_CONFIG_SECRET, Secret } from "../../common/model/secret.model";
import { SecretService } from "../../common/service/secret-service";
import { SharedDataService } from "../../common/service/shared-data-service";

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
      override activatedRoute: ActivatedRoute) {
        super(router, sharedData, service, dialog, activatedRoute);
    }

    getPageConfig(): PageConfig {
      return PAGE_CONFIG_SECRET;
    }
}