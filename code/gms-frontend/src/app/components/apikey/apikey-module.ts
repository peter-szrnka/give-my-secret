import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { SharedDataService } from "../../common/service/shared-data-service";
import { ApiKeyDetailComponent } from "./apikey-detail.component";
import { ApiKeyListComponent } from "./apikey-list.component";
import { ApiKeyDetailResolver } from "./resolver/apikey-detail.resolver";
import { ApiKeyListResolver } from "./resolver/apikey-list.resolver";
import { ApiKeyService } from "./service/apikey-service";
import { NavButtonVisibilityPipe } from "../../common/components/pipes/nav-button-visibility.pipe";

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
        GmsComponentsModule,
        MomentPipe,
        NavButtonVisibilityPipe
    ], providers: [
        SharedDataService, ApiKeyService, ApiKeyListResolver, ApiKeyDetailResolver,
        provideHttpClient(withInterceptorsFromDi())
    ] })
  export class ApiKeyModule { }