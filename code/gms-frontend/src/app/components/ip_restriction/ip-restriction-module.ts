import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { NavButtonVisibilityPipe } from "../../common/components/pipes/nav-button-visibility.pipe";
import { SplashComponent } from "../../common/components/splash/splash.component";
import { StatusToggleComponent } from "../../common/components/status-toggle/status-toggle.component";
import { SharedDataService } from "../../common/service/shared-data-service";
import { IprestrictionDetailComponent } from "./ip-restriction-detail.component";
import { IpRestrictionListComponent } from "./ip-restriction-list.component";
import { IpRestrictionDetailResolver } from "./resolver/ip-restriction-detail.resolver";
import { IpRestrictionListResolver } from "./resolver/ip-restriction-list.resolver";
import { IpRestrictionService } from "./service/ip-restriction.service";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        IpRestrictionListComponent, IprestrictionDetailComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA], 
    imports: [
        AngularMaterialModule,
        FormsModule,
        BrowserModule,
        AppRoutingModule,
        SplashComponent,
        MomentPipe,
        NavBackComponent,
        NavButtonVisibilityPipe,
        StatusToggleComponent,
        TranslatorModule
    ], providers: [
        SharedDataService, IpRestrictionService, IpRestrictionListResolver, IpRestrictionDetailResolver,
        provideHttpClient(withInterceptorsFromDi())
    ] })
  export class IpRestrictionModule { }