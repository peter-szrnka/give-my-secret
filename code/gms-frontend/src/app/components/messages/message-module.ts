import { HttpClientModule } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { MessageListComponent } from "./message-list.component";
import { MessageService } from "./service/message-service";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ 
      MessageListComponent
    ],
    imports: [
        AngularMaterialModule,
        BrowserModule,
        HttpClientModule,
        AppRoutingModule,
        GmsComponentsModule,
        PipesModule
    ],
    providers: [MessageService],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
  export class MessageModule { }