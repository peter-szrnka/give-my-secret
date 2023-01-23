import { Injectable } from "@angular/core";
import { EMPTY_USER_DATA, UserData } from "../model/user-data.model";
import { SharedDataService } from "../service/shared-data-service";
import { SplashScreenStateService } from "../service/splash-screen-service";
import { UserService } from "../service/user-service";
import { DetailDataResolver } from "./save-detail-data.resolver";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class UserDetailResolver extends DetailDataResolver<UserData, UserService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : UserService) {
        super(sharedData, splashScreenStateService, service);
    }

    protected getEmptyResponse(): UserData {
        return EMPTY_USER_DATA;
    }
}