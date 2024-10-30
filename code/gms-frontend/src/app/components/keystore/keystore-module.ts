import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { RouterLink } from "@angular/router";
import { AngularMaterialModule } from "../../angular-material-module";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { NavButtonVisibilityPipe } from "../../common/components/pipes/nav-button-visibility.pipe";
import { SplashComponent } from "../../common/components/splash/splash.component";
import { StatusToggleComponent } from "../../common/components/status-toggle/status-toggle.component";
import { KeystoreDetailComponent } from "./keystore-detail.component";
import { KeystoreListComponent } from "./keystore-list.component";
import { KeystoreDetailResolver } from "./resolver/keystore-detail.resolver";
import { KeystoreListResolver } from "./resolver/keystore-list.resolver";
import { KeystoreService } from "./service/keystore-service";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        KeystoreListComponent, KeystoreDetailComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA], 
    imports: [
        AngularMaterialModule,
        FormsModule,
        BrowserModule,
        RouterLink,
        SplashComponent,
        NavBackComponent,
        MomentPipe,
        NavButtonVisibilityPipe,
        StatusToggleComponent,
        TranslatorModule
    ], providers: [
        KeystoreService, KeystoreListResolver, KeystoreDetailResolver,
        provideHttpClient(withInterceptorsFromDi())
    ] })
  export class KeystoreModule { }