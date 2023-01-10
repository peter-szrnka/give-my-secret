import { PageConfig } from "./common.model"

export interface Secret {
    id? : number,
    secretId? : string,
    keystoreId? : number,
    keystoreAliasId? : number,
    status : string,
    value : string,
    creationDate? : Date,
    lastUpdated?: Date,
    lastRotated?: Date,
    rotationPeriod: string,
    rotationEnabled? : boolean,
    returnDecrypted? : boolean,
    apiKeyRestrictions : number[]
}

export const EMPTY_SECRET : Secret = {
    status: "ACTIVE",
    value: "",
    rotationPeriod: "HOURLY",
    apiKeyRestrictions : [],
    rotationEnabled : true,
    returnDecrypted : false
}

export const PAGE_CONFIG_SECRET : PageConfig = {
    scope: "secret",
    label: "Secret"
};