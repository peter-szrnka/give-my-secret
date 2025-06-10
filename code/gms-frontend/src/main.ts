import { enableProdMode } from '@angular/core';

import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { MAT_SNACK_BAR_DEFAULT_OPTIONS } from '@angular/material/snack-bar';
import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { AppComponent } from './app/app.component';
import { ENV_CONFIG, routes } from './app/app.config';
import { AuthInterceptor } from './app/common/interceptor/auth-interceptor';
import { CsrfTokenInterceptor } from './app/common/interceptor/csrf-token-interceptor';
import { MockInterceptor } from './app/common/interceptor/mock-interceptor';
import { environment } from './environments/environment';

if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes/*, withDebugTracing()*/),
    { provide: ENV_CONFIG, useValue: environment },
    { provide: HTTP_INTERCEPTORS, useClass: CsrfTokenInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: MockInterceptor, multi: true },
    { provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: { duration: 2500 } },
    provideHttpClient(withInterceptorsFromDi())
]
});
