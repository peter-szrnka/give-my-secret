import { Component } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseSaveableDetailComponent } from "../../common/components/abstractions/base-saveable-detail.component";
import { Announcement, PAGE_CONFIG_ANNOUNCEMENT } from "../../common/model/announcement.model";
import { PageConfig } from "../../common/model/common.model";
import { AnnouncementService } from "../../common/service/announcement-service";
import { SharedDataService } from "../../common/service/shared-data-service";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'announcement-detail',
    templateUrl: './announcement-detail.component.html',
    styleUrls : ['./announcement-detail.component.scss']
})
export class AnnouncementDetailComponent extends BaseSaveableDetailComponent<Announcement, AnnouncementService> {

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