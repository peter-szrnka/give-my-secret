import { Injectable } from "@angular/core";
import { Keystore } from "../model/keystore.model";
import { KeystoreList } from "../model/keystore-list";
import { KeystoreService } from "../service/keystore-service";
import { SplashScreenStateService } from "../service/splash-screen-service";
import { ListResolver } from "./list-data.resolver";
import { SharedDataService } from "../service/shared-data-service";

@Injectable()
export class KeystoreListResolver extends ListResolver<Keystore, KeystoreList, KeystoreService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : KeystoreService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getOrderProperty(): string {
        return "creationDate";
    }
}