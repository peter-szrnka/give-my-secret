import { Injectable } from "@angular/core";
import { PAGE_CONFIG_USER, UserData } from "../model/user-data.model";
import { UserDataList } from "../model/user-list.model";
import { UserService } from "../service/user-service";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { ListResolverV2 } from "../../../common/components/abstractions/resolver/list-data.resolver";
import { PageConfig } from "../../../common/model/common.model";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class UserListResolver extends ListResolverV2<UserData, UserDataList, UserService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : UserService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getPageConfig(): PageConfig {
        return PAGE_CONFIG_USER;
    }

    override getOrderProperty(): string {
        return "creationDate";
    }
}