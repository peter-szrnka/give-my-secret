import { HttpClientModule } from "@angular/common/http";
import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { NavMenuModule } from "../menu/nav-menu.module";
import { HelpComponent } from "./help.compontent";
import { ErrorCodeService } from "./service/error-code.service";
import { ErrorCodeResolver } from "./resolver/error-code.resolver";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ 
      HelpComponent
    ],
    imports: [
        AngularMaterialModule,
        BrowserModule,
        BrowserAnimationsModule,
        HttpClientModule,
        AppRoutingModule,
        NavMenuModule
    ],
    providers: [ErrorCodeService, ErrorCodeResolver],
    exports : [ HelpComponent ]
  })
  export class HelpModule { }