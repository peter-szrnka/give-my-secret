import { PageConfig } from "./common.model";
import { KeystoreAlias } from "./keystore-alias.model";

export interface Keystore {
    id? : number,
    userId? : number,
    name? : string,
    fileName? : string,
    type? : string,
    description : string,
    status? : string,
    credential? : string,
    creationDate? : Date,
    aliases : KeystoreAlias[]
}

export const EMPTY_KEYSTORE : Keystore = {
    description: "",
    type: "JKS",
    status: "ACTIVE",
    aliases: []
};

export const PAGE_CONFIG_KEYSTORE : PageConfig = {
    scope: "keystore",
    label: "Keystore"
};