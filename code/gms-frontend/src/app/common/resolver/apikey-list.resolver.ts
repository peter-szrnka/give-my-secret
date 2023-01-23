import { Injectable } from "@angular/core";
import { ApiKey } from "../model/apikey.model";
import { ApiKeyList } from "../model/apikey-list.model";
import { ApiKeyService } from "../service/apikey-service";
import { SplashScreenStateService } from "../service/splash-screen-service";
import { ListResolver } from "./list-data.resolver";
import { SharedDataService } from "../service/shared-data-service";

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