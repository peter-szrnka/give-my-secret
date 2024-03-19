import { BaseDetail } from "../../../common/model/base-detail.model";
import { PageConfig } from "../../../common/model/common.model";

/**
 * @author Peter Szrnka
 */
export interface UserData extends BaseDetail {
    username? : string,
    name? : string,
    email? : string,
    credential? : string,
    id? : number,
    status? : string,
    creationDate? : Date,
    roles : string[]
}

export const EMPTY_USER_DATA : UserData = {
    credential: undefined,
    roles: []
};

export const PAGE_CONFIG_USER : PageConfig = {
    scope: "user",
    label: "User"
};