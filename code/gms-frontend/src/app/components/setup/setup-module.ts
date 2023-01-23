import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AngularMaterialModule } from "../../angular-material-module";
import { SetupComponent } from "./setup.component";
import { FormsModule } from "@angular/forms";
import { WINDOW_TOKEN } from "../../window.provider";

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
        BrowserAnimationsModule
    ],
    providers: [{ provide: WINDOW_TOKEN, useValue: window }],
    schemas : [ CUSTOM_ELEMENTS_SCHEMA ]
  })
  export class SetupModule { }