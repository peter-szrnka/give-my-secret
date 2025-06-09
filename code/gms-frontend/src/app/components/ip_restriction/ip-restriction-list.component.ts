import { Component, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";
import { PageConfig } from "../../common/model/common.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { IpRestriction, PAGE_CONFIG_IP_RESTRICTION } from "./model/ip-restriction.model";
import { IpRestrictionService } from "./service/ip-restriction.service";
import { FormsModule } from "@angular/forms";
import { AngularMaterialModule } from "../../angular-material-module";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'ip-restriction-list',
    templateUrl: './ip-restriction-list.component.html',
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    imports: [
        AngularMaterialModule,
        FormsModule,
        RouterModule,
        MomentPipe,
        NavBackComponent,
        TranslatorModule,
        InformationMessageComponent
    ]
})
export class IpRestrictionListComponent extends BaseListComponent<IpRestriction, IpRestrictionService> {

    ipRestrictionColumns: string[] = ['id', 'ipPattern', 'status', 'creationDate', 'operations'];

    constructor(
        override router: Router,
        override sharedData: SharedDataService,
        public override service: IpRestrictionService,
        public override dialogService: DialogService,
        override activatedRoute: ActivatedRoute) {
        super(router, sharedData, service, dialogService, activatedRoute);
    }

    getPageConfig(): PageConfig {
        return PAGE_CONFIG_IP_RESTRICTION;
    }
}