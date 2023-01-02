import { HttpClientModule } from "@angular/common/http";
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { SettingsSummaryComponent } from "./settings-summary.component";

@NgModule({
    declarations: [ 
      SettingsSummaryComponent
     ],
    imports: [
        AngularMaterialModule,
        FormsModule,
        BrowserModule,
        HttpClientModule,
        AppRoutingModule
    ],
    providers: [ 
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
export class SettingsModule {}