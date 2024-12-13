import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { SettingsSummaryComponent } from "./settings-summary.component";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        SettingsSummaryComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA], imports: [AngularMaterialModule,
    FormsModule,
    BrowserModule,
    AppRoutingModule,
    TranslatorModule, InformationMessageComponent], providers: [
        provideHttpClient(withInterceptorsFromDi())
    ] })
export class SettingsModule {}