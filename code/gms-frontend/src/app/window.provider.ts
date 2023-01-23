import { InjectionToken } from "@angular/core";

/**
 * Injection token for browser window object
 * 
 * @author Peter Szrnka
 */
export const WINDOW_TOKEN = new InjectionToken<Window>('Window');