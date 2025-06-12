import { enableProdMode } from '@angular/core';

import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { MAT_SNACK_BAR_DEFAULT_OPTIONS } from '@angular/material/snack-bar';
import { bootstrapApplication } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideRouter, withDebugTracing } from '@angular/router';
import { AppComponent } from './app/app.component';
import { ENV_CONFIG, routes } from './app/app.config';
import { AuthInterceptor } from './app/common/interceptor/auth-interceptor';
import { CsrfTokenInterceptor } from './app/common/interceptor/csrf-token-interceptor';
import { MockInterceptor } from './app/common/interceptor/mock-empty-interceptor';
import { WINDOW_TOKEN } from './app/window.provider';
import { environment } from './environments/environment';

/**
 * @author Peter Szrnka
 */
const APP_CONFIG = {
  providers: [
    provideRouter(routes, 
      ...(environment.production ? [] : [withDebugTracing()])
    ),
    { provide: ENV_CONFIG, useValue: environment },
    { provide: WINDOW_TOKEN, useFactory: () => (typeof window !== 'undefined' ? window : null) },
    { provide: HTTP_INTERCEPTORS, useClass: CsrfTokenInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: MockInterceptor, multi: true },
    { provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: { duration: 2500 } },
    provideHttpClient(withInterceptorsFromDi()),
    provideAnimations()
  ]
};

if (environment.production) {
  enableProdMode();
}

/**
 * @author Peter Szrnka
 */
bootstrapApplication(AppComponent, APP_CONFIG);
