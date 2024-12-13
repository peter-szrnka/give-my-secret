import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { VerifyComponent } from "./verify.component";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        VerifyComponent
    ],
    exports: [VerifyComponent], imports: [AngularMaterialModule,
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutingModule,
        FormsModule,
        InformationMessageComponent,
    TranslatorModule], providers: [provideHttpClient(withInterceptorsFromDi())] })
  export class VerifyModule { }