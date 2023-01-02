import { HttpClientModule } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { SecretService } from "../../common/service/secret-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SecretDetailComponent } from "./secret-detail.component";
import { SecretListComponent } from "./secret-list.component";

@NgModule({
    declarations: [ 
      SecretListComponent, SecretDetailComponent
     ],
    imports: [
        AngularMaterialModule,
        FormsModule,
        BrowserModule,
        HttpClientModule,
        AppRoutingModule,
        GmsComponentsModule,
        PipesModule
    ],
    providers: [ 
      SharedDataService, SecretService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
  export class SecretModule { }