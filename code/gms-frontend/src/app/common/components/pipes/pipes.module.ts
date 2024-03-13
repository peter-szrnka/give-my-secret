
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { MomentPipe } from "./date-formatter.pipe";
import { NavButtonVisibilityPipe } from "./nav-button-visibility.pipe";
import { CommonModule } from "@angular/common";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ MomentPipe, NavButtonVisibilityPipe ],
    imports: [
        FormsModule,
        CommonModule,
    ],
    exports : [
      MomentPipe, NavButtonVisibilityPipe
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
  })
  export class PipesModule { }