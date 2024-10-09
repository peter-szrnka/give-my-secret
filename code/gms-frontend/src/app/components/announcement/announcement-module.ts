import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { AnnouncementDetailComponent } from "./announcement-detail.component";
import { AnnouncementListComponent } from "./announcement-list.component";
import { AnnouncementDetailResolver } from "./resolver/announcement-detail.resolver";
import { AnnouncementListResolver } from "./resolver/announcement-list.resolver";
import { AnnouncementService } from "./service/announcement-service";
import { NavButtonVisibilityPipe } from "../../common/components/pipes/nav-button-visibility.pipe";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        AnnouncementListComponent, AnnouncementDetailComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA], imports: [
        AngularMaterialModule,
        FormsModule,
        BrowserModule,
        AppRoutingModule,
        GmsComponentsModule,
        MomentPipe,
        NavButtonVisibilityPipe
    ], providers: [
        AnnouncementService, AnnouncementListResolver, AnnouncementDetailResolver,
        provideHttpClient(withInterceptorsFromDi())
    ] })
export class AnnouncementModule {}