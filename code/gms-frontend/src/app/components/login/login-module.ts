import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { LoginComponent } from "./login.component";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        LoginComponent
    ],
    exports: [LoginComponent], imports: [AngularMaterialModule,
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    FormsModule, TranslatorModule], providers: [provideHttpClient(withInterceptorsFromDi())] })
  export class LoginModule { }