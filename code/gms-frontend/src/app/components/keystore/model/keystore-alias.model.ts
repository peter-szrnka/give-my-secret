/**
 * @author Peter Szrnka
 */
export interface KeystoreAlias {
    id? : number;
    alias : string;
    aliasCredential? : string;
    operation : string;
    algorithm? : string;
}