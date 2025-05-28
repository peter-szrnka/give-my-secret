import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AngularMaterialModule } from "../../angular-material-module";
import { SetupComponent } from "./setup.component";
import { FormsModule } from "@angular/forms";
import { WINDOW_TOKEN } from "../../window.provider";
import { SetupService } from "./service/setup-service";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { VmOptionsComponent } from "../../common/components/vm-options/vm-options.component";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ 
      SetupComponent
    ],
    imports: [
    AngularMaterialModule,
    BrowserModule,
    FormsModule,
    BrowserAnimationsModule,
    TranslatorModule,
    InformationMessageComponent,
    VmOptionsComponent
],
    providers: [{ provide: WINDOW_TOKEN, useValue: window }, SetupService],
    schemas : [ CUSTOM_ELEMENTS_SCHEMA ]
  })
  export class SetupModule { }