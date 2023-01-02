import { Injectable } from "@angular/core";
import { Secret } from "../model/secret.model";
import { SecretList } from "../model/secret-list.model";
import { SecretService } from "../service/secret-service";
import { SplashScreenStateService } from "../service/splash-screen-service";
import { ListResolver } from "./list-data.resolver";
import { SharedDataService } from "../service/shared-data-service";

@Injectable()
export class SecretListResolver extends ListResolver<Secret, SecretList, SecretService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : SecretService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getOrderProperty(): string {
        return "creationDate";
    }
}