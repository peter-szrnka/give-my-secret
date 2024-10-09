import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { SystemPropertyListResolver } from "./resolver/system-property-list.resolver";
import { SystemPropertyService } from "./service/system-property.service";
import { SystemPropertyListComponent } from "./system-property-list.component";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        SystemPropertyListComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA], 
    imports: [
        AngularMaterialModule,
        ReactiveFormsModule,
        FormsModule,
        BrowserModule,
        AppRoutingModule,
        GmsComponentsModule,
        MomentPipe
    ], 
    providers: [
        SystemPropertyService, SystemPropertyListResolver,
        provideHttpClient(withInterceptorsFromDi())
    ] 
})
  export class SystemPropertyModule { }