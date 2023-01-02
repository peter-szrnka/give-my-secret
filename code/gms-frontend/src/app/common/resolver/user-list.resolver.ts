import { Injectable } from "@angular/core";
import { UserData } from "../model/user-data.model";
import { UserDataList } from "../model/user-list.model";
import { SharedDataService } from "../service/shared-data-service";
import { SplashScreenStateService } from "../service/splash-screen-service";
import { UserService } from "../service/user-service";
import { ListResolver } from "./list-data.resolver";

@Injectable()
export class UserListResolver extends ListResolver<UserData, UserDataList, UserService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : UserService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getOrderProperty(): string {
        return "creationDate";
    }
}