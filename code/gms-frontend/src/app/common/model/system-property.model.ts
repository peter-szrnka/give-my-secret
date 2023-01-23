/**
 * @author Peter Szrnka
 */
export interface SystemProperty {
    key : string;
    value : string;
    type : string;
    lastModified? : Date;
    factoryValue : boolean;
    valueSet? : string[];
    mode? : string;
}