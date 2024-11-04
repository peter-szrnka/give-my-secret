
import { CommonModule } from "@angular/common";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { ConfirmDeleteDialog } from "./confirm-delete/confirm-delete-dialog.component";
import { InfoDialog } from "./info-dialog/info-dialog.component";
import { MomentPipe } from "./pipes/date-formatter.pipe";
import { NavButtonVisibilityPipe } from "./pipes/nav-button-visibility.pipe";
import { TranslatorModule } from "./pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [
        ConfirmDeleteDialog, InfoDialog
    ],
    exports: [
        ConfirmDeleteDialog, InfoDialog
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
    imports: [
        CommonModule,
        AngularMaterialModule,
        FormsModule,
        AppRoutingModule,
        MomentPipe,
        NavButtonVisibilityPipe,
        TranslatorModule
    ]
})
  export class GmsComponentsModule { }