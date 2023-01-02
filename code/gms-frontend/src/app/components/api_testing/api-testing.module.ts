import { HttpClientModule } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { ApiTestingComponent } from "./api-testing.component";

@NgModule({
    declarations: [ 
        ApiTestingComponent
    ],
    imports: [
        AngularMaterialModule,
        FormsModule,
        BrowserModule,
        HttpClientModule,
        AppRoutingModule,
        GmsComponentsModule
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
  export class ApiTestingModule { }