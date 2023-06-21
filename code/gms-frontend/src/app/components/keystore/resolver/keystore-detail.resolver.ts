import { Injectable } from "@angular/core";
import { EMPTY_KEYSTORE, Keystore } from "../model/keystore.model";
import { KeystoreService } from "../service/keystore-service";
import { DetailDataResolverV2 } from "../../../common/components/abstractions/resolver/save-detail-data.resolver";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class KeystoreDetailResolver extends DetailDataResolverV2<Keystore, KeystoreService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : KeystoreService) {
        super(sharedData, splashScreenStateService, service);
    }

    protected getEmptyResponse(): Keystore {
        return EMPTY_KEYSTORE;
    }
}