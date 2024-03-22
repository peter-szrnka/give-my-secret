import { BaseDetail } from "../../../common/model/base-detail.model";
import { PageConfig } from "../../../common/model/common.model"
import { IpRestriction } from "../../ip_restriction/model/ip-restriction.model";

/**
 * @author Peter Szrnka
 */
export interface Secret extends BaseDetail {
    id? : number,
    secretId? : string,
    keystoreId? : number,
    keystoreAliasId? : number,
    status : string,
    type : string,
    value : string,
    creationDate? : Date,
    lastUpdated?: Date,
    lastRotated?: Date,
    rotationPeriod: string,
    rotationEnabled? : boolean,
    returnDecrypted? : boolean,
    apiKeyRestrictions : number[],
    ipRestrictions? : IpRestriction[]
}

export const EMPTY_SECRET : Secret = {
    status: "ACTIVE",
    type : "SIMPLE_CREDENTIAL",
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