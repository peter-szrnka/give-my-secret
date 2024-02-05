/**
 * @author Peter Szrnka
 */
export interface Message {
    id? : number,
    message : string,
    creationDate? : Date,
    opened : boolean;
    actionPath?: string;
}