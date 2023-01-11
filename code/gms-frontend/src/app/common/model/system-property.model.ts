export interface SystemProperty {
    key : string;
    value : string;
    lastModified : Date;
    factoryValue : boolean;
    valueSet? : string[];
    mode? : string;
}