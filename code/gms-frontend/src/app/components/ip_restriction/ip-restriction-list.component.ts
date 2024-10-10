import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";
import { PageConfig } from "../../common/model/common.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { IpRestriction, PAGE_CONFIG_IP_RESTRICTION } from "./model/ip-restriction.model";
import { IpRestrictionService } from "./service/ip-restriction.service";

export const COPY_MESSAGE = "Api key value copied to clipboard!";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'ip-restriction-list',
    templateUrl: './ip-restriction-list.component.html',
    styleUrls: ['./ip-restriction-list.component.scss']
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