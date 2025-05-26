
/**
 * @author Peter Szrnka
 */
export interface ButtonConfig {
    label? : string,
    url? : string,
    callFunction?: Function,
    primary? : boolean,
    visibilityCondition? : boolean;
    enabled? : boolean;
}