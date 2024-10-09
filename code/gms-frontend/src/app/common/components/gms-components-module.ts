
import { CommonModule } from "@angular/common";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { AutomaticLogoutComponent } from "./automatic-logout/automatic-logout.component";
import { ConfirmDeleteDialog } from "./confirm-delete/confirm-delete-dialog.component";
import { InfoDialog } from "./info-dialog/info-dialog.component";
import { NavBackComponent } from "./nav-back/nav-back.component";
import { MomentPipe } from "./pipes/date-formatter.pipe";
import { NavButtonVisibilityPipe } from "./pipes/nav-button-visibility.pipe";
import { SplashComponent } from "./splash/splash.component";
import { StatusToggleComponent } from "./status-toggle/status-toggle.component";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [
        ConfirmDeleteDialog, InfoDialog, SplashComponent, StatusToggleComponent, NavBackComponent, AutomaticLogoutComponent
    ],
    exports: [
        ConfirmDeleteDialog, InfoDialog, SplashComponent, StatusToggleComponent, NavBackComponent, AutomaticLogoutComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
    imports: [
        CommonModule,
        AngularMaterialModule,
        FormsModule,
        AppRoutingModule,
        MomentPipe,
        NavButtonVisibilityPipe
    ]
})
  export class GmsComponentsModule { }