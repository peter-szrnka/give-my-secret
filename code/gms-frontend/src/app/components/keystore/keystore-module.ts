import { HttpClientModule } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { KeystoreService } from "./service/keystore-service";
import { KeystoreDetailComponent } from "./keystore-detail.component";
import { KeystoreListComponent } from "./keystore-list.component";
import { KeystoreListResolver } from "./resolver/keystore-list.resolver";
import { KeystoreDetailResolver } from "./resolver/keystore-detail.resolver";

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
      KeystoreService, KeystoreListResolver, KeystoreDetailResolver
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
  export class KeystoreModule { }