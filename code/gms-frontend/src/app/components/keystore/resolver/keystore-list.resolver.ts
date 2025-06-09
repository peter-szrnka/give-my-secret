import { Injectable } from "@angular/core";
import { Keystore, PAGE_CONFIG_KEYSTORE } from "../model/keystore.model";
import { KeystoreList } from "../model/keystore-list";
import { KeystoreService } from "../service/keystore-service";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { ListResolver } from "../../../common/components/abstractions/resolver/list-data.resolver";
import { PageConfig } from "../../../common/model/common.model";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root'})
export class KeystoreListResolver extends ListResolver<Keystore, KeystoreList, KeystoreService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : KeystoreService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getPageConfig(): PageConfig {
        return PAGE_CONFIG_KEYSTORE;
    }

    override getOrderProperty(): string {
        return "creationDate";
    }
}