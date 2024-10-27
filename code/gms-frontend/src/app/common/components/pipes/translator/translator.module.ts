import { NgModule } from "@angular/core";
import { TranslatorService } from "../../../service/translator-service";
import { TranslatorPipe } from "./translator.pipe";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [
        TranslatorPipe
    ],
    providers: [
        TranslatorService
    ],
    exports: [
        TranslatorPipe
    ],
    imports: [
    ]
})
export class TranslatorModule { }