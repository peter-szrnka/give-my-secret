import { Injectable } from "@angular/core";
import { DetailDataResolver } from "../../../common/components/abstractions/resolver/save-detail-data.resolver";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { ApiKey, EMPTY_API_KEY } from "../model/apikey.model";
import { ApiKeyService } from "../service/apikey-service";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class ApiKeyDetailResolver extends DetailDataResolver<ApiKey, ApiKeyService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : ApiKeyService) {
        super(sharedData, splashScreenStateService, service);
    }

    protected getEmptyResponse(): ApiKey {
        return EMPTY_API_KEY;
    }
}