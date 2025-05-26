import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseSaveableDetailComponent } from "../../common/components/abstractions/component/base-saveable-detail.component";
import { ButtonConfig } from "../../common/components/nav-back/button-config";
import { PageConfig } from "../../common/model/common.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { IpRestriction, PAGE_CONFIG_IP_RESTRICTION } from "./model/ip-restriction.model";
import { IpRestrictionService } from "./service/ip-restriction.service";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'ip-restriction-key-detail',
    templateUrl: './ip-restriction-detail.component.html',
    standalone: false
})
export class IprestrictionDetailComponent extends BaseSaveableDetailComponent<IpRestriction, IpRestrictionService> {

    buttonConfig: ButtonConfig[] = [
        { primary: true, url: '/ip_restriction/list', label: 'navback.back2List' }
    ];

    constructor(
        protected override router: Router,
        protected override sharedData: SharedDataService,
        protected override service: IpRestrictionService,
        public override dialogService: DialogService,
        protected override activatedRoute: ActivatedRoute,
        protected override splashScreenStateService: SplashScreenStateService) {
        super(router, sharedData, service, dialogService, activatedRoute, splashScreenStateService);
    }

    override getPageConfig(): PageConfig {
        return PAGE_CONFIG_IP_RESTRICTION;
    }
}