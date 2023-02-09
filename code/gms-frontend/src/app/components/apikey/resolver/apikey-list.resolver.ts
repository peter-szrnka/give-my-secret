import { Injectable } from "@angular/core";
import { ListResolver } from "../../../common/resolver/list-data.resolver";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { ApiKeyList } from "../model/apikey-list.model";
import { ApiKey } from "../model/apikey.model";
import { ApiKeyService } from "../service/apikey-service";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class ApiKeyListResolver extends ListResolver<ApiKey, ApiKeyList, ApiKeyService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : ApiKeyService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getOrderProperty(): string {
        return "creationDate";
    }
}