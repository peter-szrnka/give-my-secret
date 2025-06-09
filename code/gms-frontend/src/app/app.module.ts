import { CUSTOM_ELEMENTS_SCHEMA, InjectionToken, NgModule } from '@angular/core';

import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// Material Modules
import { MAT_SNACK_BAR_DEFAULT_OPTIONS } from '@angular/material/snack-bar';
import { AngularMaterialModule } from './angular-material-module';
import { AuthInterceptor } from './common/interceptor/auth-interceptor';
import { CsrfTokenInterceptor } from './common/interceptor/csrf-token-interceptor';
import { MockInterceptor } from './common/interceptor/mock-interceptor';
import { ServiceModule } from './common/service/service-module';
import { AnnouncementModule } from './components/announcement/announcement-module';
import { EventModule } from './components/event/event-module';
import { HeaderModule } from './components/header/header-module';
import { IpRestrictionModule } from './components/ip_restriction/ip-restriction-module';
import { NavMenuModule } from './components/menu/nav-menu.module';
import { SystemPropertyModule } from './components/system_property/system-property-module';
import { UserModule } from './components/user/user-module';

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
        HeaderModule,
        ServiceModule,
        UserModule,
        EventModule,
        AnnouncementModule,
        SystemPropertyModule,
        IpRestrictionModule
    ], 
    providers: [
        { provide: ENV_CONFIG, useValue: environment },
        { provide: HTTP_INTERCEPTORS, useClass: CsrfTokenInterceptor, multi: true },
        { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
        { provide: HTTP_INTERCEPTORS, useClass: MockInterceptor, multi: true },
        { provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: { duration: 2500 } },
        provideHttpClient(withInterceptorsFromDi()),
    ] })
export class AppModule { }
