import { Observable } from "rxjs";

/**
 * @author Peter Szrnka
 */
export interface ButtonConfig {
    label? : string,
    customText?: string,
    type: string,
    url? : string,
    callFunction?: Function,
    primary? : boolean,
    visibilityCondition? : boolean;
    enabled? : boolean;
}