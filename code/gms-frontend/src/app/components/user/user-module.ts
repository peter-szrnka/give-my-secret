import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { SplashComponent } from "../../common/components/splash/splash.component";
import { StatusToggleComponent } from "../../common/components/status-toggle/status-toggle.component";
import { UserDetailResolver } from "./resolver/user-detail.resolver";
import { UserListResolver } from "./resolver/user-list.resolver";
import { UserService } from "./service/user-service";
import { UserDetailComponent } from "./user-detail.component";
import { UserListComponent } from "./user-list.component";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        UserListComponent, UserDetailComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA], 
    imports: [AngularMaterialModule,
        FormsModule,
        BrowserModule,
        AppRoutingModule,
        SplashComponent,
        MomentPipe,
        NavBackComponent,
        StatusToggleComponent
    ], 
    providers: [
        UserService, UserListResolver, UserDetailResolver,
        provideHttpClient(withInterceptorsFromDi())
    ] })
  export class UserModule { }