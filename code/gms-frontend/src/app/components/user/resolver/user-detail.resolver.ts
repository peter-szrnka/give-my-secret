import { Injectable } from "@angular/core";
import { EMPTY_USER_DATA, UserData } from "../model/user-data.model";
import { UserService } from "../service/user-service";
import { DetailDataResolver } from "../../../common/components/abstractions/resolver/save-detail-data.resolver";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class UserDetailResolver extends DetailDataResolver<UserData, UserService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : UserService) {
        super(sharedData, splashScreenStateService, service);
    }

    protected getEmptyResponse(): UserData {
        return EMPTY_USER_DATA;
    }
}