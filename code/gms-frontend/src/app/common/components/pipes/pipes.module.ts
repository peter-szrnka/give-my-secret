
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { MomentPipe } from "./date-formatter.pipe";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ MomentPipe ],
    imports: [
        FormsModule,
        BrowserModule,
    ],
    exports : [
      MomentPipe
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
  export class PipesModule { }