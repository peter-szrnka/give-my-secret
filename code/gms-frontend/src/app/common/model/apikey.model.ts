import { PageConfig } from "./common.model"

/**
 * @author Peter Szrnka
 */
export interface ApiKey {
    id? : number,
    userId? : number,
    name? : string,
    value? : string,
    description : string,
    status? : string,
    creationDate? : Date
}

export const EMPTY_API_KEY : ApiKey = {
    description: "",
    status : "ACTIVE"
}

export const PAGE_CONFIG_API_KEY : PageConfig = {
    scope: "apikey",
    label: "API key"
};