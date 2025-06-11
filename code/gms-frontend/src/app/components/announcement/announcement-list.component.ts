import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { AngularMaterialModule } from "../../angular-material-module";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
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
    imports: [
        AngularMaterialModule,
        FormsModule,
        RouterModule,
        MomentPipe,
        NavBackComponent,
        InformationMessageComponent,
        TranslatorModule
    ]
})
export class AnnouncementListComponent extends BaseListComponent<Announcement, AnnouncementService> {

    columns: string[] = ['id', 'title', 'announcementDate', 'operations'];

    constructor(
        override router: Router,
        override sharedData: SharedDataService,
        override service: AnnouncementService,
        public override dialogService: DialogService,
        override activatedRoute: ActivatedRoute) {
        super(router, sharedData, service, dialogService, activatedRoute);
    }

    getPageConfig(): PageConfig {
        return PAGE_CONFIG_ANNOUNCEMENT;
    }
}