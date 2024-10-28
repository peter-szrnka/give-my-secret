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
import { ApiKeyDetailComponent } from "./apikey-detail.component";
import { ApiKeyListComponent } from "./apikey-list.component";
import { ApiKeyDetailResolver } from "./resolver/apikey-detail.resolver";
import { ApiKeyListResolver } from "./resolver/apikey-list.resolver";
import { ApiKeyService } from "./service/apikey-service";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        ApiKeyListComponent, ApiKeyDetailComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA], 
    imports: [
        AngularMaterialModule,
        FormsModule,
        BrowserModule,
        AppRoutingModule,
        SplashComponent,
        NavBackComponent,
        MomentPipe,
        NavButtonVisibilityPipe,
        StatusToggleComponent,
        TranslatorModule
    ], providers: [
        SharedDataService, ApiKeyService, ApiKeyListResolver, ApiKeyDetailResolver,
        provideHttpClient(withInterceptorsFromDi())
    ] })
  export class ApiKeyModule { }