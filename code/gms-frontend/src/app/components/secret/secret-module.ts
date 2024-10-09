import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { SecretDetailResolver } from "./resolver/secret-detail.resolver";
import { SecretListResolver } from "./resolver/secret-list.resolver";
import { SecretDetailComponent } from "./secret-detail.component";
import { SecretListComponent } from "./secret-list.component";
import { SecretService } from "./service/secret-service";
import { NavButtonVisibilityPipe } from "../../common/components/pipes/nav-button-visibility.pipe";

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
        GmsComponentsModule,
        MomentPipe,
        NavButtonVisibilityPipe
    ], 
    providers: [
        SecretService, SecretListResolver, SecretDetailResolver,
        provideHttpClient(withInterceptorsFromDi())
    ] })
  export class SecretModule { }