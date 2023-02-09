import { HttpClientModule } from "@angular/common/http";
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SystemPropertyService } from "./service/system-property.service";
import { SystemPropertyListComponent } from "./system-property-list.component";
import { SystemPropertyListResolver } from "./resolver/system-property-list.resolver";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ 
      SystemPropertyListComponent
     ],
    imports: [
        AngularMaterialModule,
        ReactiveFormsModule,
        FormsModule,
        BrowserModule,
        HttpClientModule,
        AppRoutingModule,
        GmsComponentsModule,
        PipesModule
    ],
    providers: [ 
      SharedDataService, SystemPropertyService, SystemPropertyListResolver
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
  export class SystemPropertyModule { }