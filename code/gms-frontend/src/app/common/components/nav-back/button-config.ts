/**
 * @author Peter Szrnka
 */
export interface ButtonConfig {
    label : string,
    url? : string,
    callFunction?: (() => void);
    primary : boolean,
    visibilityCondition? : boolean;
}