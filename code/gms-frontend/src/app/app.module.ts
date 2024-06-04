import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

import { HTTP_INTERCEPTORS, HttpClientModule, provideHttpClient } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ServiceWorkerModule } from '@angular/service-worker';
import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// Material Modules
import { MAT_SNACK_BAR_DEFAULT_OPTIONS } from '@angular/material/snack-bar';
import { AngularMaterialModule } from './angular-material-module';
import { GmsComponentsModule } from './common/components/gms-components-module';
import { PipesModule } from './common/components/pipes/pipes.module';
import { AuthInterceptor } from './common/interceptor/auth-interceptor';
import { ServiceModule } from './common/service/service-module';
import { AnnouncementModule } from './components/announcement/announcement-module';
import { ApiTestingModule } from './components/api_testing/api-testing.module';
import { ApiKeyModule } from './components/apikey/apikey-module';
import { EventModule } from './components/event/event-module';
import { HeaderModule } from './components/header/header-module';
import { HomeModule } from './components/home/home-module';
import { IpRestrictionModule } from './components/ip_restriction/ip-restriction-module';
import { KeystoreModule } from './components/keystore/keystore-module';
import { LoginModule } from './components/login/login-module';
import { NavMenuModule } from './components/menu/nav-menu.module';
import { MessageModule } from './components/messages/message-module';
import { RequestPasswordResetModule } from './components/password_reset/request-password-reset.module';
import { SecretModule } from './components/secret/secret-module';
import { SettingsModule } from './components/settings/settings-module';
import { SetupModule } from './components/setup/setup-module';
import { SystemPropertyModule } from './components/system_property/system-property-module';
import { UserModule } from './components/user/user-module';
import { VerifyModule } from './components/verify/verify-module';
import { HelpModule } from './components/help/help-module';
import { MockInterceptor } from './common/interceptor/mock-interceptor';

/**
 * @author Peter Szrnka
 */
@NgModule({
  declarations: [ 
    AppComponent
   ],
  imports: [
    AngularMaterialModule,
    FormsModule,
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: environment.production,
      // Register the ServiceWorker as soon as the application is stable
      // or after 30 seconds (whichever comes first).
      registrationStrategy: 'registerWhenStable:30000'
    }),
    BrowserAnimationsModule,
    PipesModule,
    GmsComponentsModule,
    
    // Main application modules
    NavMenuModule,
    HomeModule,
    SetupModule,
    LoginModule,
    RequestPasswordResetModule,
    HeaderModule,
    ServiceModule,
    KeystoreModule,
    ApiKeyModule,
    SecretModule,
    UserModule,
    EventModule,
    AnnouncementModule,
    SettingsModule,
    MessageModule,
    ApiTestingModule,
    SystemPropertyModule,
    VerifyModule,
    IpRestrictionModule,
    HelpModule
  ],
  providers: [ 
    provideHttpClient(),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: MockInterceptor, multi: true },
    { provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: {duration: 2500 }},
  ],
  bootstrap: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppModule { }
