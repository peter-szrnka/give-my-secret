import { CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA } from "@angular/core";
import { AngularMaterialModule } from "../../angular-material-module";
import { NavMenuComponent } from "./nav-menu.component";
import { RouterModule } from "@angular/router";
import { TranslatorPipe } from "../../common/components/pipes/translator/translator.pipe";

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
        TranslatorPipe
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
  })
  export class NavMenuModule { }