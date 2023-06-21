import { Injectable } from "@angular/core";
import { Announcement, EMPTY_ANNOUNCEMENT } from "../model/announcement.model";
import { AnnouncementService } from "../service/announcement-service";
import { DetailDataResolver } from "../../../common/components/abstractions/resolver/save-detail-data.resolver";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class AnnouncementDetailResolver extends DetailDataResolver<Announcement, AnnouncementService> {

    constructor(
        protected override sharedData : SharedDataService, 
        protected override splashScreenStateService: SplashScreenStateService, 
        protected override service : AnnouncementService) {
        super(sharedData, splashScreenStateService, service);
    }

    protected getEmptyResponse(): Announcement {
        return EMPTY_ANNOUNCEMENT;
    }
}