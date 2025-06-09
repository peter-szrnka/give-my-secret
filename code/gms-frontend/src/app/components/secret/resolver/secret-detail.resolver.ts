import { Injectable } from "@angular/core";
import { EMPTY_SECRET, Secret } from "../model/secret.model";
import { SecretService } from "../service/secret-service";
import { DetailDataResolver } from "../../../common/components/abstractions/resolver/save-detail-data.resolver";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root'})
export class SecretDetailResolver extends DetailDataResolver<Secret, SecretService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : SecretService) {
        super(sharedData, splashScreenStateService, service);
    }

    protected getEmptyResponse(): Secret {
        return EMPTY_SECRET;
    }
}