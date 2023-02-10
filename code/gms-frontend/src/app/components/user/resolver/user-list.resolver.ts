import { Injectable } from "@angular/core";
import { UserData } from "../model/user-data.model";
import { UserDataList } from "../model/user-list.model";
import { UserService } from "../service/user-service";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { ListResolver } from "../../../common/components/abstractions/resolver/list-data.resolver";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class UserListResolver extends ListResolver<UserData, UserDataList, UserService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : UserService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getOrderProperty(): string {
        return "creationDate";
    }
}