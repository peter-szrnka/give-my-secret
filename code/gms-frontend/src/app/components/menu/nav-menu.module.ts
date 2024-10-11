import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
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
        AppRoutingModule
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
  export class NavMenuModule { }