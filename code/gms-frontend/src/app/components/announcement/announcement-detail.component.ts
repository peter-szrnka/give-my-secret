import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { AngularMaterialModule } from "../../angular-material-module";
import { BaseSaveableDetailComponent } from "../../common/components/abstractions/component/base-saveable-detail.component";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { PageConfig } from "../../common/model/common.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { Announcement, PAGE_CONFIG_ANNOUNCEMENT } from "./model/announcement.model";
import { AnnouncementService } from "./service/announcement-service";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'announcement-detail',
    templateUrl: './announcement-detail.component.html',
    imports: [
        AngularMaterialModule,
        FormsModule,
        MomentPipe,
        NavBackComponent,
        InformationMessageComponent,
        TranslatorModule
    ]
})
export class AnnouncementDetailComponent extends BaseSaveableDetailComponent<Announcement, AnnouncementService> {

  constructor(
    override router : Router,
    override sharedData : SharedDataService, 
    override service : AnnouncementService,
    public override dialogService: DialogService,
    override activatedRoute: ActivatedRoute,
    protected override splashScreenStateService: SplashScreenStateService) {
      super(router, sharedData, service, dialogService, activatedRoute, splashScreenStateService);
  }

  getPageConfig(): PageConfig {
    return PAGE_CONFIG_ANNOUNCEMENT;
  }
}