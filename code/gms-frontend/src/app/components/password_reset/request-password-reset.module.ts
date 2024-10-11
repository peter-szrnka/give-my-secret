import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { RequestPasswordResetComponent } from "./request-password-reset.component";
import { ResetPasswordRequestService } from "./service/request-password-reset.service";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        RequestPasswordResetComponent
    ],
    exports: [RequestPasswordResetComponent], imports: [AngularMaterialModule,
        AppRoutingModule,
        FormsModule], providers: [ResetPasswordRequestService, provideHttpClient(withInterceptorsFromDi())] })
  export class RequestPasswordResetModule { }