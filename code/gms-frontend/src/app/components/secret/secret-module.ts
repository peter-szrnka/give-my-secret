import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { NavButtonVisibilityPipe } from "../../common/components/pipes/nav-button-visibility.pipe";
import { SplashComponent } from "../../common/components/splash/splash.component";
import { SecretDetailResolver } from "./resolver/secret-detail.resolver";
import { SecretListResolver } from "./resolver/secret-list.resolver";
import { SecretDetailComponent } from "./secret-detail.component";
import { SecretListComponent } from "./secret-list.component";
import { SecretService } from "./service/secret-service";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { StatusToggleComponent } from "../../common/components/status-toggle/status-toggle.component";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        SecretListComponent, SecretDetailComponent
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
        StatusToggleComponent
    ], 
    providers: [
        SecretService, SecretListResolver, SecretDetailResolver,
        provideHttpClient(withInterceptorsFromDi())
    ] })
  export class SecretModule { }