import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";
import { PageConfig } from "../../common/model/common.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { Announcement, PAGE_CONFIG_ANNOUNCEMENT } from "./model/announcement.model";
import { AnnouncementService } from "./service/announcement-service";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'announcement-list-component',
    templateUrl: './announcement-list.component.html',
    standalone: false
})
export class AnnouncementListComponent extends BaseListComponent<Announcement, AnnouncementService> {

    columns: string[] = [ 'id', 'title', 'announcementDate', 'operations' ];

    constructor(
      override router : Router,
      override sharedData : SharedDataService, 
      override service : AnnouncementService,
      public override dialogService: DialogService,
      override activatedRoute: ActivatedRoute) {
        super(router, sharedData, service, dialogService, activatedRoute);
    }

    getPageConfig(): PageConfig {
        return PAGE_CONFIG_ANNOUNCEMENT;
    }
}