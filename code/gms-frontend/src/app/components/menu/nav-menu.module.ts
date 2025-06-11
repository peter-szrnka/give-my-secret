import { CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA } from "@angular/core";
import { AngularMaterialModule } from "../../angular-material-module";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { NavMenuComponent } from "./nav-menu.component";
import { RouterModule } from "@angular/router";

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
    RouterModule,
    TranslatorModule
],
    schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
  })
  export class NavMenuModule { }