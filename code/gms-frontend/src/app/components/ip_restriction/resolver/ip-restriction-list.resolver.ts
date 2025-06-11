import { Injectable } from "@angular/core";
import { ListResolver } from "../../../common/components/abstractions/resolver/list-data.resolver";
import { PageConfig } from "../../../common/model/common.model";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { IpRestrictionList } from "../model/ip-restriction-list.model";
import { IpRestriction, PAGE_CONFIG_IP_RESTRICTION } from "../model/ip-restriction.model";
import { IpRestrictionService } from "../service/ip-restriction.service";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class IpRestrictionListResolver extends ListResolver<IpRestriction, IpRestrictionList, IpRestrictionService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : IpRestrictionService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getPageConfig(): PageConfig {
        return PAGE_CONFIG_IP_RESTRICTION;
    }

    override getOrderProperty(): string {
        return "id";
    }
}