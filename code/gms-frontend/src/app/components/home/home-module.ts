import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { HomeComponent } from "./home.component";
import { HomeService } from "./service/home.service";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        HomeComponent
    ], imports: [
        AngularMaterialModule,
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutingModule,
        MomentPipe,
        TranslatorModule
    ], providers: [HomeService, provideHttpClient(withInterceptorsFromDi())] })
  export class HomeModule { }