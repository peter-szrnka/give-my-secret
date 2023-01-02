
export type ClickFunction = () => void;

export interface ButtonConfig {
    label : string,
    url? : string
    primary : boolean,
    visibilityCondition? : boolean;
}