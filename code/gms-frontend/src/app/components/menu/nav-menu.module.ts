import { CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA } from "@angular/core";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { NavMenuComponent } from "./nav-menu.component";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ 
        NavMenuComponent
    ],
    exports : [
        NavMenuComponent
    ],
    imports: [
    AngularMaterialModule,
    AppRoutingModule,
    TranslatorModule
],
    schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
  })
  export class NavMenuModule { }