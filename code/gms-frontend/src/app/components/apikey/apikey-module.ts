import { HttpClientModule } from "@angular/common/http";
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { ApiKeyService } from "../../common/service/apikey-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { ApiKeyDetailComponent } from "./apikey-detail.component";
import { ApiKeyListComponent } from "./apikey-list.component";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ 
      ApiKeyListComponent, ApiKeyDetailComponent
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
      SharedDataService, ApiKeyService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
  export class ApiKeyModule { }