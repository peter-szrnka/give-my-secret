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
    scope: "ipRestriction",
    label: "IP Restriction"
};

export const EMPTY_IP_RESTRICTION : IpRestriction = {
    status: "ACTIVE",
    ipPattern: "",
    allow: false
}