import { PageConfig } from "./common.model";

export interface Keystore {
    id? : number,
    userId? : number,
    name? : string,
    fileName? : string,
    type? : string,
    description : string,
    status? : string,
    credential? : string,
    alias? : string,
    aliasCredential? : string,
    creationDate? : Date
}

export const EMPTY_KEYSTORE : Keystore = {
    description: "",
    type : "JKS",
    status : "ACTIVE"
};

export const PAGE_CONFIG_KEYSTORE : PageConfig = {
    scope: "keystore",
    label: "Keystore"
};