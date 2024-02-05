import { NgModule } from "@angular/core";
import { RequestPasswordResetComponent } from "./request-password-reset.component";
import { HttpClientModule } from "@angular/common/http";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { ResetPasswordRequestService } from "./service/request-password-reset.service";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ 
      RequestPasswordResetComponent
    ],
    imports: [
        AngularMaterialModule,
        BrowserModule,
        BrowserAnimationsModule,
        HttpClientModule,
        AppRoutingModule,
        FormsModule
    ],
    providers: [ResetPasswordRequestService],
    exports : [ RequestPasswordResetComponent ]
  })
  export class RequestPasswordResetModule { }