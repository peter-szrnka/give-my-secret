import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AngularMaterialModule } from "../../angular-material-module";
import { SetupComponent } from "./setup.component";
import { HttpClientModule } from "@angular/common/http";
import { FormsModule } from "@angular/forms";

@NgModule({
    declarations: [ 
      SetupComponent
    ],
    imports: [
        AngularMaterialModule,
        BrowserModule,
        FormsModule,
        BrowserAnimationsModule,
        HttpClientModule
    ],
    providers: [],
    exports : [ SetupComponent ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
  export class SetupModule { }