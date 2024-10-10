import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { AutomaticLogoutComponent } from "../../common/components/automatic-logout/automatic-logout.component";
import { SplashComponent } from "../../common/components/splash/splash.component";
import { NavMenuModule } from "../menu/nav-menu.module";
import { HeaderComponent } from "./header.component";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        HeaderComponent
    ],
    exports: [HeaderComponent], 
    imports: [
        AngularMaterialModule,
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutingModule,
        NavMenuModule,
        AutomaticLogoutComponent,
        SplashComponent
    ], 
    providers: [provideHttpClient(withInterceptorsFromDi())] })
  export class HeaderModule { }