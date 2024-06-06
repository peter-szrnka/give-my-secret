import { HttpClientModule } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { AuthService } from "./auth-service";
import { SharedDataService } from "./shared-data-service";
import { SplashScreenStateService } from "./splash-screen-service";
import { SecureStorageService } from "./secure-storage.service";
import { ClipboardService } from "./clipboard-service";
import { InformationService } from "./info-service";
import { LoggerService } from "./logger-service";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ 
      
     ],
    imports: [
        HttpClientModule,
    ],
    providers: [ 
      SharedDataService, AuthService, SplashScreenStateService, SecureStorageService, ClipboardService, InformationService, LoggerService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
  })
  export class ServiceModule { }