import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { HomeComponent } from "./home.component";
import { HomeService } from "./service/home.service";

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
        InformationMessageComponent,
        TranslatorModule
    ], providers: [HomeService, provideHttpClient(withInterceptorsFromDi())] })
  export class HomeModule { }