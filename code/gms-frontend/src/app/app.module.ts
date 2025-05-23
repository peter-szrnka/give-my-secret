import { CUSTOM_ELEMENTS_SCHEMA, InjectionToken, NgModule } from '@angular/core';

import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// Material Modules
import { MAT_SNACK_BAR_DEFAULT_OPTIONS } from '@angular/material/snack-bar';
import { AngularMaterialModule } from './angular-material-module';
import { AuthInterceptor } from './common/interceptor/auth-interceptor';
import { MockInterceptor } from './common/interceptor/mock-interceptor';
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
import { CsrfTokenInterceptor } from './common/interceptor/csrf-token-interceptor';

export const ENV_CONFIG = new InjectionToken('gmsEnvConfig');

/**
 * @author Peter Szrnka
 */
@NgModule({ declarations: [
        AppComponent
    ],
    bootstrap: [AppComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA], 
    imports: [
        AngularMaterialModule,
        AppRoutingModule,
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
        IpRestrictionModule
    ], 
    providers: [
        provideHttpClient(),
        { provide: ENV_CONFIG, useValue: environment },
        { provide: HTTP_INTERCEPTORS, useClass: CsrfTokenInterceptor, multi: true },
        { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
        { provide: HTTP_INTERCEPTORS, useClass: MockInterceptor, multi: true },
        { provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: { duration: 2500 } },
        provideHttpClient(withInterceptorsFromDi()),
    ] })
export class AppModule { }
