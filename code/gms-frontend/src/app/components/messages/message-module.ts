import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA, NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../../common/components/gms-components-module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { MessageListComponent } from "./message-list.component";
import { MessageService } from "./service/message-service";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        MessageListComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA], 
    imports: [
        AngularMaterialModule,
        BrowserModule,
        AppRoutingModule,
        GmsComponentsModule,
        MomentPipe,
        NavBackComponent,
        TranslatorModule,
        InformationMessageComponent
    ], providers: [
        MessageService, provideHttpClient(withInterceptorsFromDi())
    ]
})
  export class MessageModule { }