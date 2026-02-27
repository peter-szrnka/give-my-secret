import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { AngularMaterialModule } from "../../angular-material-module";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { StatusToggleComponent } from "../../common/components/status-toggle/status-toggle.component";
import { PageConfig } from "../../common/model/common.model";
import { ClipboardService } from "../../common/service/clipboard-service";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { PAGE_CONFIG_SECRET, Secret } from "./model/secret.model";
import { SecretService } from "./service/secret-service";
import { TranslatorPipe } from "../../common/components/pipes/translator/translator.pipe";

export const COPY_SECRET_ID_MESSAGE = "Secret ID copied to clipboard!";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'secret-list',
    templateUrl: './secret-list.component.html',
    imports: [
      AngularMaterialModule,
      FormsModule,
      NavBackComponent,
      MomentPipe,
      TranslatorPipe,
      RouterModule,
      StatusToggleComponent,
      InformationMessageComponent
  ]
})
export class SecretListComponent extends BaseListComponent<Secret, SecretService> {
    secretColumns: string[] = ['id', 'secretId', 'status', 'lastUpdated', 'lastRotated', 'rotationPeriod', 'operations'];

    constructor(
      override router : Router,
      override sharedData : SharedDataService, 
      override service : SecretService,
      public override dialogService: DialogService,
      override activatedRoute: ActivatedRoute,
      private readonly clipboardService: ClipboardService) {
        super(router, sharedData, service, dialogService, activatedRoute);
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