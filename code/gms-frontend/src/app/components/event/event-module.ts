import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { SplashComponent } from "../../common/components/splash/splash.component";
import { EventListComponent } from "./event-list.component";
import { EventListResolver } from "./resolver/event-list.resolver";
import { EventService } from "./service/event-service";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        EventListComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA], 
    imports: [
        AngularMaterialModule,
        FormsModule,
        BrowserModule,
        AppRoutingModule,
        SplashComponent,
        MomentPipe,
        NavBackComponent
    ], providers: [
        EventService, EventListResolver, provideHttpClient(withInterceptorsFromDi())
    ]
})
  export class EventModule { }