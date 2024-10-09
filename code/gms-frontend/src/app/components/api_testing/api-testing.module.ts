import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { ApiTestingComponent } from "./api-testing.component";
import { ApiTestingService } from "./service/api-testing-service";

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        ApiTestingComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA], 
    imports: [
        AngularMaterialModule,
        FormsModule,
        BrowserModule,
        AppRoutingModule
    ], providers: [ApiTestingService, provideHttpClient(withInterceptorsFromDi())] })
  export class ApiTestingModule { }