import { Injectable } from "@angular/core";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { ApiKeyList } from "../model/apikey-list.model";
import { ApiKey, PAGE_CONFIG_API_KEY } from "../model/apikey.model";
import { ApiKeyService } from "../service/apikey-service";
import { ListResolver } from "../../../common/components/abstractions/resolver/list-data.resolver";
import { PageConfig } from "../../../common/model/common.model";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class ApiKeyListResolver extends ListResolver<ApiKey, ApiKeyList, ApiKeyService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : ApiKeyService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getPageConfig(): PageConfig {
        return PAGE_CONFIG_API_KEY;
    }

    override getOrderProperty(): string {
        return "creationDate";
    }
}