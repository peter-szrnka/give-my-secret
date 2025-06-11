import { Injectable } from "@angular/core";
import { AnnouncementService } from "../service/announcement-service";
import { Announcement, PAGE_CONFIG_ANNOUNCEMENT } from "../model/announcement.model";
import { AnnouncementList } from "../model/annoucement-list.model";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { ListResolver } from "../../../common/components/abstractions/resolver/list-data.resolver";
import { PageConfig } from "../../../common/model/common.model";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class AnnouncementListResolver extends ListResolver<Announcement, AnnouncementList, AnnouncementService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : AnnouncementService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getPageConfig(): PageConfig {
        return PAGE_CONFIG_ANNOUNCEMENT;
    }

    override getOrderProperty(): string {
        return "announcementDate";
    }
}