import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { SettingsSummaryComponent } from "./settings-summary.component";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";

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
    TranslatorModule, GmsComponentsModule], providers: [
        provideHttpClient(withInterceptorsFromDi())
    ] })
export class SettingsModule {}