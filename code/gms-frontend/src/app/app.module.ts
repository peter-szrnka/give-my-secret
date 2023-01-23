import { NgModule, CUSTOM_ELEMENTS_SCHEMA  } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule  } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ServiceWorkerModule } from '@angular/service-worker';
import { environment } from '../environments/environment';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

// Material Modules
import { AngularMaterialModule } from './angular-material-module';
import { AuthInterceptor } from './common/interceptor/auth-interceptor';
import { KeystoreModule } from './components/keystore/keystore-module';
import { ServiceModule } from './common/service/service-module';
import { ApiKeyModule } from './components/apikey/apikey-module';
import { SecretModule } from './components/secret/secret-module';
import { UserModule } from './components/user/user-module';
import { GmsComponentsModule } from './common/components/gms-components-module';
import { EventModule } from './components/event/event-module';
import { AnnouncementModule } from './components/announcement/announcement-module';
import { SettingsModule } from './components/settings/settings-module';
import { MAT_SNACK_BAR_DEFAULT_OPTIONS } from '@angular/material/snack-bar';
import { MessageModule } from './components/messages/message-module';
import { ApiTestingModule } from './components/api_testing/api-testing.module';
import { ResolverModule } from './common/resolver/resolver.module';
import { SetupModule } from './components/setup/setup-module';
import { LoginModule } from './components/login/login-module';
import { HomeModule } from './components/home/home-module';
import { HeaderModule } from './components/header/header-module';
import { PipesModule } from './common/components/pipes/pipes.module';
import { SystemPropertyModule } from './components/system_property/system-property-module';

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
    HomeModule,
    SetupModule,
    LoginModule,
    HeaderModule,
    ResolverModule,
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
    PipesModule
  ],
  providers: [ 
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: {duration: 2500 }}
  ],
  bootstrap: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppModule { }
