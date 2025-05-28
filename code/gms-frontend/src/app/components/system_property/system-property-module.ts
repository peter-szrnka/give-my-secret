import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { SplashComponent } from "../../common/components/splash/splash.component";
import { SystemPropertyListResolver } from "./resolver/system-property-list.resolver";
import { SystemPropertyService } from "./service/system-property.service";
import { SystemPropertyListComponent } from "./system-property-list.component";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { VmOptionsComponent } from "../../common/components/vm-options/vm-options.component";

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
        SplashComponent,
        MomentPipe,
        NavBackComponent,
        TranslatorModule,
        VmOptionsComponent
    ], 
    providers: [
        SystemPropertyService, SystemPropertyListResolver,
        provideHttpClient(withInterceptorsFromDi())
    ] 
})
  export class SystemPropertyModule { }