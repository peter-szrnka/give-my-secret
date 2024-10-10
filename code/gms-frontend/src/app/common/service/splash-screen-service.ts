import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class SplashScreenStateService {

    public splashScreenSubject$ = new Subject<boolean>();

    start() {
        this.splashScreenSubject$.next(true);
    }

    stop() {
       this.splashScreenSubject$.next(false);
    }
}