import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { NgModule } from "@angular/core";
import { RouterModule } from "@angular/router";
import { AngularMaterialModule } from "../../angular-material-module";
import { AutomaticLogoutComponent } from "../../common/components/automatic-logout/automatic-logout.component";
import { SplashComponent } from "../../common/components/splash/splash.component";
import { NavMenuModule } from "../menu/nav-menu.module";
import { HeaderComponent } from "./header.component";
import { TranslatorPipe } from "../../common/components/pipes/translator/translator.pipe";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        HeaderComponent
    ],
    exports: [HeaderComponent], 
    imports: [
        AngularMaterialModule,
        NavMenuModule,
        RouterModule,
        AutomaticLogoutComponent,
        SplashComponent,
        TranslatorPipe
    ], 
    providers: [provideHttpClient(withInterceptorsFromDi())] })
  export class HeaderModule { }