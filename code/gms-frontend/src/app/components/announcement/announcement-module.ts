import { HttpClientModule } from "@angular/common/http";
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { AnnouncementService } from "../../common/service/announcement-service";
import { AnnouncementDetailComponent } from "./announcement-detail.component";
import { AnnouncementListComponent } from "./announcement-list.component";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ 
        AnnouncementListComponent, AnnouncementDetailComponent
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
      AnnouncementService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
export class AnnouncementModule {}