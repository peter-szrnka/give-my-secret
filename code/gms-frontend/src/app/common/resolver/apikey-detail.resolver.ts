import { Injectable } from "@angular/core";
import { ApiKey, EMPTY_API_KEY } from "../model/apikey.model";
import { ApiKeyService } from "../service/apikey-service";
import { SharedDataService } from "../service/shared-data-service";
import { SplashScreenStateService } from "../service/splash-screen-service";
import { DetailDataResolver } from "./save-detail-data.resolver";

@Injectable()
export class ApiKeyDetailResolver extends DetailDataResolver<ApiKey, ApiKeyService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : ApiKeyService) {
        super(sharedData, splashScreenStateService, service);
    }

    protected getEmptyResponse(): ApiKey {
        return EMPTY_API_KEY;
    }
}