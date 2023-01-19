import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AngularMaterialModule } from "../../angular-material-module";
import { SetupComponent } from "./setup.component";
import { FormsModule } from "@angular/forms";

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
    providers: [],
    exports : [ SetupComponent ]
  })
  export class SetupModule { }