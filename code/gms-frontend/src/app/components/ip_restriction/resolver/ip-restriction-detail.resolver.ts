import { Injectable } from "@angular/core";
import { DetailDataResolver } from "../../../common/components/abstractions/resolver/save-detail-data.resolver";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { EMPTY_IP_RESTRICTION, IpRestriction } from "../model/ip-restriction.model";
import { IpRestrictionService } from "../service/ip-restriction.service";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class IpRestrictionDetailResolver extends DetailDataResolver<IpRestriction, IpRestrictionService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : IpRestrictionService) {
        super(sharedData, splashScreenStateService, service);
    }

    protected getEmptyResponse(): IpRestriction {
        return EMPTY_IP_RESTRICTION;
    }
}