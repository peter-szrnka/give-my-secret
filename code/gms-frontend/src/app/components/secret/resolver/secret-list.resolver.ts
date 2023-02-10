import { Injectable } from "@angular/core";
import { Secret } from "../model/secret.model";
import { SecretList } from "../model/secret-list.model";
import { SecretService } from "../service/secret-service";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { ListResolver } from "../../../common/components/abstractions/resolver/list-data.resolver";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class SecretListResolver extends ListResolver<Secret, SecretList, SecretService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : SecretService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getOrderProperty(): string {
        return "creationDate";
    }
}