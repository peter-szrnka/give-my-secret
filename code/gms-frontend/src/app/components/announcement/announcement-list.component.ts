import {  Component } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseListComponent } from "../../common/components/abstractions/base-list.component";
import { Announcement, PAGE_CONFIG_ANNOUNCEMENT } from "../../common/model/announcement.model";
import { PageConfig } from "../../common/model/common.model";
import { AnnouncementService } from "../../common/service/announcement-service";
import { SharedDataService } from "../../common/service/shared-data-service";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'announcement-list-component',
    templateUrl: './announcement-list.component.html',
    styleUrls: ['./announcement-list.component.scss']
})
export class AnnouncementListComponent extends BaseListComponent<Announcement, AnnouncementService> {

    columns: string[] = [ 'id', 'title', 'announcementDate', 'operations' ];

    constructor(
      override router : Router,
      override sharedData : SharedDataService, 
      override service : AnnouncementService,
      public override dialog: MatDialog,
      override activatedRoute: ActivatedRoute) {
        super(router, sharedData, service, dialog, activatedRoute);
    }

    getPageConfig(): PageConfig {
        return PAGE_CONFIG_ANNOUNCEMENT;
    }
}