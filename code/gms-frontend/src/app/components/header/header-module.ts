import { HttpClientModule } from "@angular/common/http";
import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { HeaderComponent } from "./header.component";
import { NavMenuModule } from "../menu/nav-menu.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ 
      HeaderComponent
    ],
    imports: [
    AngularMaterialModule,
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    AppRoutingModule,
    NavMenuModule,
    GmsComponentsModule
],
    providers: [],
    exports : [ HeaderComponent ]
  })
  export class HeaderModule { }