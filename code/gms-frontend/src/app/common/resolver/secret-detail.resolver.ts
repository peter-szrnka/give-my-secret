import { Injectable } from "@angular/core";
import { EMPTY_SECRET, Secret } from "../model/secret.model";
import { SecretService } from "../service/secret-service";
import { SharedDataService } from "../service/shared-data-service";
import { SplashScreenStateService } from "../service/splash-screen-service";
import { DetailDataResolver } from "./save-detail-data.resolver";

@Injectable()
export class SecretDetailResolver extends DetailDataResolver<Secret, SecretService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : SecretService) {
        super(sharedData, splashScreenStateService, service);
    }

    protected getEmptyResponse(): Secret {
        return EMPTY_SECRET;
    }
}