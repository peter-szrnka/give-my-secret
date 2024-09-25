import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { SharedDataService } from "../../common/service/shared-data-service";
import { IpRestrictionService } from "./service/ip-restriction.service";
import { IpRestrictionListResolver } from "./resolver/ip-restriction-list.resolver";
import { IpRestrictionDetailResolver } from "./resolver/ip-restriction-detail.resolver";
import { IpRestrictionListComponent } from "./ip-restriction-list.component";
import { IprestrictionDetailComponent } from "./ip-restriction-detail.component";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        IpRestrictionListComponent, IprestrictionDetailComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA], imports: [AngularMaterialModule,
        FormsModule,
        BrowserModule,
        AppRoutingModule,
        GmsComponentsModule,
        PipesModule], providers: [
        SharedDataService, IpRestrictionService, IpRestrictionListResolver, IpRestrictionDetailResolver,
        provideHttpClient(withInterceptorsFromDi())
    ] })
  export class IpRestrictionModule { }