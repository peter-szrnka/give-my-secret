import { Injectable } from "@angular/core";
import { PAGE_CONFIG_SECRET, Secret } from "../model/secret.model";
import { SecretList } from "../model/secret-list.model";
import { SecretService } from "../service/secret-service";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { ListResolverV2 } from "../../../common/components/abstractions/resolver/list-data.resolver";
import { PageConfig } from "../../../common/model/common.model";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class SecretListResolver extends ListResolverV2<Secret, SecretList, SecretService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : SecretService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getPageConfig(): PageConfig {
        return PAGE_CONFIG_SECRET;
    }

    override getOrderProperty(): string {
        return "creationDate";
    }
}