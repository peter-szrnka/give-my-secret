import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { AngularMaterialModule } from "../../angular-material-module";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { StatusToggleComponent } from "../../common/components/status-toggle/status-toggle.component";
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
    imports: [
        AngularMaterialModule,
        FormsModule,
        RouterModule,
        NavBackComponent,
        MomentPipe,
        StatusToggleComponent,
        TranslatorModule,
        InformationMessageComponent
    ]
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