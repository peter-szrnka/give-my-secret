import { PageConfig } from "../../../common/model/common.model";

/**
 * @author Peter Szrnka
 */
export interface IpRestriction {
    id?: number,
    secretId?: number,
    status?: string,
    ipPattern: string,
    allow: boolean,
    creationDate? : Date,
    lastModified? : Date;
}

export const PAGE_CONFIG_IP_RESTRICTION : PageConfig = {
    scope: "ip_restriction",
    label: "IP restriction"
};

export const EMPTY_IP_RESTRICTION : IpRestriction = {
    status: "ACTIVE",
    ipPattern: "",
    allow: false
}