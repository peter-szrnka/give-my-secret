/**
 * @author Peter Szrnka
 */
export interface SystemProperty {
    key : string;
    value : string;
    type : string;
    category : string;
    lastModified? : Date;
    factoryValue : boolean;
    valueSet? : string[];
    mode? : string;
    callbackMethod?: string;
}