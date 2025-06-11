import { enableProdMode } from '@angular/core';

import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { APP_CONFIG } from './app/app.config';
import { environment } from './environments/environment';

if (environment.production) {
  enableProdMode();
}

/**
 * @author Peter Szrnka
 */
bootstrapApplication(AppComponent, APP_CONFIG);
