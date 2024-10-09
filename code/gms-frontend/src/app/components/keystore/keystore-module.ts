import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { NavButtonVisibilityPipe } from "../../common/components/pipes/nav-button-visibility.pipe";
import { KeystoreDetailComponent } from "./keystore-detail.component";
import { KeystoreListComponent } from "./keystore-list.component";
import { KeystoreDetailResolver } from "./resolver/keystore-detail.resolver";
import { KeystoreListResolver } from "./resolver/keystore-list.resolver";
import { KeystoreService } from "./service/keystore-service";

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
        AppRoutingModule,
        GmsComponentsModule,
        MomentPipe,
        NavButtonVisibilityPipe
    ], providers: [
        KeystoreService, KeystoreListResolver, KeystoreDetailResolver,
        provideHttpClient(withInterceptorsFromDi())
    ] })
  export class KeystoreModule { }