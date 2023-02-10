import { Injectable } from "@angular/core";
import { Keystore } from "../model/keystore.model";
import { KeystoreList } from "../model/keystore-list";
import { KeystoreService } from "../service/keystore-service";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { ListResolver } from "../../../common/components/abstractions/resolver/list-data.resolver";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class KeystoreListResolver extends ListResolver<Keystore, KeystoreList, KeystoreService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : KeystoreService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getOrderProperty(): string {
        return "creationDate";
    }
}