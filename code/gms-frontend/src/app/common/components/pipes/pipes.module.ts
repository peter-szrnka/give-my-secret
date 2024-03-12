
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { MomentPipe } from "./date-formatter.pipe";
import { NavButtonVisibilityPipe } from "./nav-button-visibility.pipe";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ MomentPipe, NavButtonVisibilityPipe ],
    imports: [
        FormsModule,
        BrowserModule,
    ],
    exports : [
      MomentPipe, NavButtonVisibilityPipe
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
  export class PipesModule { }