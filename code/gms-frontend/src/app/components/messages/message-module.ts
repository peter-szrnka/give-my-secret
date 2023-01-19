import { HttpClientModule } from "@angular/common/http";
import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { MessageListComponent } from "./message-list.component";

@NgModule({
    declarations: [ 
      MessageListComponent
    ],
    imports: [
        AngularMaterialModule,
        BrowserModule,
        //HttpClientModule,
        AppRoutingModule,
        GmsComponentsModule,
        PipesModule
    ],
    providers: []
  })
  export class MessageModule { }