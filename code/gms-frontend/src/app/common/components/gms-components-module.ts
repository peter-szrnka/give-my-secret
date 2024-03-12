
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { ConfirmDeleteDialog } from "./confirm-delete/confirm-delete-dialog.component";
import { InfoDialog } from "./info-dialog/info-dialog.component";
import { NavBackComponent } from "./nav-back/nav-back.component";
import { SplashComponent } from "./splash/splash.component";
import { StatusToggleComponent } from "./status-toggle/status-toggle.component";
import { PipesModule } from "./pipes/pipes.module";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [
        ConfirmDeleteDialog, InfoDialog, SplashComponent, NavBackComponent, StatusToggleComponent
    ],
    exports: [
        ConfirmDeleteDialog, InfoDialog, SplashComponent, NavBackComponent, StatusToggleComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    imports: [
        AngularMaterialModule,
        FormsModule,
        BrowserModule,
        AppRoutingModule,
        PipesModule
    ]
})
  export class GmsComponentsModule { }