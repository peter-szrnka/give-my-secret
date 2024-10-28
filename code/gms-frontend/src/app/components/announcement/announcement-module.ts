import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { NavButtonVisibilityPipe } from "../../common/components/pipes/nav-button-visibility.pipe";
import { AnnouncementDetailComponent } from "./announcement-detail.component";
import { AnnouncementListComponent } from "./announcement-list.component";
import { AnnouncementDetailResolver } from "./resolver/announcement-detail.resolver";
import { AnnouncementListResolver } from "./resolver/announcement-list.resolver";
import { AnnouncementService } from "./service/announcement-service";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";

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
        MomentPipe,
        NavBackComponent,
        NavButtonVisibilityPipe,
        TranslatorModule
    ], providers: [
        AnnouncementService, AnnouncementListResolver, AnnouncementDetailResolver,
        provideHttpClient(withInterceptorsFromDi())
    ] })
export class AnnouncementModule {}