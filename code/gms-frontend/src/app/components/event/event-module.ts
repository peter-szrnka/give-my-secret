import { HttpClientModule } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { EventListComponent } from "./event-list.component";
import { EventService } from "./service/event-service";
import { EventListResolver } from "./resolver/event-list.resolver";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ 
      EventListComponent
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
    providers: [ EventService, EventListResolver
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
  export class EventModule { }