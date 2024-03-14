
/**
 * @author Peter Szrnka
 */
export interface IpRestriction {
    id?: number,
    secretId?: number,
    ipPattern: string,
    allow: boolean,
    creationDate? : Date,
    lastModified? : Date;
}