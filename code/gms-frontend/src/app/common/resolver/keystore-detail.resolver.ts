import { Injectable } from "@angular/core";
import { EMPTY_KEYSTORE, Keystore } from "../model/keystore.model";
import { KeystoreService } from "../service/keystore-service";
import { SharedDataService } from "../service/shared-data-service";
import { SplashScreenStateService } from "../service/splash-screen-service";
import { DetailDataResolver } from "./save-detail-data.resolver";

@Injectable()
export class KeystoreDetailResolver extends DetailDataResolver<Keystore, KeystoreService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : KeystoreService) {
        super(sharedData, splashScreenStateService, service);
    }

    protected getEmptyResponse(): Keystore {
        return EMPTY_KEYSTORE;
    }
}