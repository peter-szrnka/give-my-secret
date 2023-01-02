import { Injectable } from "@angular/core";
import { AnnouncementList } from "../model/annoucement-list.model";
import { Announcement } from "../model/announcement.model";
import { AnnouncementService } from "../service/announcement-service";
import { SharedDataService } from "../service/shared-data-service";
import { SplashScreenStateService } from "../service/splash-screen-service";
import { ListResolver } from "./list-data.resolver";

@Injectable()
export class AnnouncementListResolver extends ListResolver<Announcement, AnnouncementList, AnnouncementService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : AnnouncementService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getOrderProperty(): string {
        return "announcementDate";
    }
}