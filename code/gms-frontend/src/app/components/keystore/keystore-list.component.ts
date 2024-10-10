import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";
import { PageConfig } from "../../common/model/common.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { Keystore, PAGE_CONFIG_KEYSTORE } from "./model/keystore.model";
import { KeystoreService } from "./service/keystore-service";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'keystore-list',
    templateUrl: './keystore-list.component.html',
    styleUrls : ['./keystore-list.component.scss']
})
export class KeystoreListComponent extends BaseListComponent<Keystore, KeystoreService> {
    keystoreColumns: string[] = ['id', 'name', 'type', 'status', 'creationDate', 'operations'];

    constructor(
      override router : Router,
      override sharedData : SharedDataService, 
      override service : KeystoreService,
      public override dialogService: DialogService,
      override activatedRoute: ActivatedRoute) {
        super(router, sharedData, service, dialogService, activatedRoute);
    }

    getPageConfig(): PageConfig {
        return PAGE_CONFIG_KEYSTORE;
    }
}