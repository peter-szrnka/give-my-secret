import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { UserService } from "./service/user-service";
import { UserDetailComponent } from "./user-detail.component";
import { UserListComponent } from "./user-list.component";
import { UserListResolver } from "./resolver/user-list.resolver";
import { UserDetailResolver } from "./resolver/user-detail.resolver";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        UserListComponent, UserDetailComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA], imports: [AngularMaterialModule,
        FormsModule,
        BrowserModule,
        AppRoutingModule,
        GmsComponentsModule,
        PipesModule], providers: [
        UserService, UserListResolver, UserDetailResolver,
        provideHttpClient(withInterceptorsFromDi())
    ] })
  export class UserModule { }