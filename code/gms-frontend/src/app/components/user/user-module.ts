import { HttpClientModule } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { SharedDataService } from "../../common/service/shared-data-service";
import { UserService } from "../../common/service/user-service";
import { UserDetailComponent } from "./user-detail.component";
import { UserListComponent } from "./user-list.component";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ 
      UserListComponent, UserDetailComponent
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
      SharedDataService, UserService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
  export class UserModule { }