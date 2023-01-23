import { HttpClientModule } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { KeystoreService } from "../../common/service/keystore-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { KeystoreDetailComponent } from "./keystore-detail.component";
import { KeystoreListComponent } from "./keystore-list.component";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ 
      KeystoreListComponent, KeystoreDetailComponent
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
      SharedDataService, KeystoreService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
  export class KeystoreModule { }